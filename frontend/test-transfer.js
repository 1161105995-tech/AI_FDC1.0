import axios from 'axios';

// 测试脚本：模拟前端提交移交申请
async function testTransferSubmission() {
  try {
    console.log('开始测试移交申请提交...');
    
    // 1. 加载文档类型树
    console.log('1. 加载文档类型树');
    let documentTypeTree = [];
    try {
      const documentTypeResponse = await axios.get('http://localhost:8080/api/document-type/tree');
      documentTypeTree = documentTypeResponse.data.data || [];
      console.log('文档类型树加载成功，共', documentTypeTree.length, '个文档类型');
    } catch (error) {
      console.warn('加载文档类型树失败，使用默认值:', error.message);
    }
    
    // 2. 加载档案创建选项
    console.log('2. 加载档案创建选项');
    let options = { documentOrganizations: [] };
    try {
      const optionsResponse = await axios.get('http://localhost:8080/api/archive/create/options');
      options = optionsResponse.data.data || { documentOrganizations: [] };
      console.log('档案创建选项加载成功，文档组织数量：', options.documentOrganizations.length);
    } catch (error) {
      console.warn('加载档案创建选项失败，使用默认值:', error.message);
    }
    
    // 3. 构建移交申请数据
    console.log('3. 构建移交申请数据');
    const transferData = {
      processDefinitionKey: 'documentTransfer',
      businessKey: `transfer_${Date.now()}`,
      businessType: 'TRANSFER',
      initiatorId: '1',
      initiatorName: '测试用户',
      variables: {
        transferForm: {
          assigneeId: '1',
          transferMethod: 'DIRECT',
          logisticsCompany: '',
          trackingNumber: '',
          remark: '测试移交申请'
        },
        transferDocuments: [
          {
            documentTypeCode: documentTypeTree.length > 0 ? documentTypeTree[0].typeCode : 'TEST_TYPE',
            businessCode: 'TEST001',
            documentOrganizationCode: options.documentOrganizations.length > 0 ? options.documentOrganizations[0].code : 'TEST_ORG',
            extFields: {}
          }
        ],
        transferMode: 'SCENARIO_B'
      }
    };
    
    // 4. 提交移交申请
    console.log('4. 提交移交申请');
    const submitResponse = await axios.post('http://localhost:8080/api/workflow/processes', transferData);
    
    if (submitResponse.data.success) {
      console.log('✅ 移交申请提交成功！');
      console.log('流程实例ID:', submitResponse.data.data.processInstanceId);
      console.log('业务键:', submitResponse.data.data.businessKey);
      return true;
    } else {
      console.log('❌ 移交申请提交失败:', submitResponse.data.message);
      return false;
    }
    
  } catch (error) {
    console.error('❌ 测试过程中出现错误:', error.message);
    if (error.response) {
      console.error('响应状态:', error.response.status);
      console.error('响应数据:', error.response.data);
    }
    return false;
  }
}

// 运行测试
testTransferSubmission().then(success => {
  if (success) {
    console.log('\n🎉 测试通过！移交申请提交功能正常。');
  } else {
    console.log('\n💥 测试失败！移交申请提交功能存在问题。');
  }
});
