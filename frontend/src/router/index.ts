import { createRouter, createWebHistory } from 'vue-router'
import ConsoleLayout from '../layouts/ConsoleLayout.vue'
import CreateArchiveView from '../views/archive-management/CreateArchiveView.vue'
import AiSearchResultsView from '../views/archive-management/AiSearchResultsView.vue'
import ArchiveQueryView from '../views/archive-management/ArchiveQueryView.vue'
import ArchiveDetailView from '../views/archive-management/ArchiveDetailView.vue'
import BaseDataArchiveFlowRuleView from '../views/base-data/BaseDataArchiveFlowRuleView.vue'
import BaseDataCompanyProjectDetailView from '../views/base-data/BaseDataCompanyProjectDetailView.vue'
import BaseDataCompanyProjectDictionaryView from '../views/base-data/BaseDataCompanyProjectDictionaryView.vue'
import BaseDataCompanyProjectListView from '../views/base-data/BaseDataCompanyProjectListView.vue'
import BaseDataDocumentOrganizationView from '../views/base-data/BaseDataDocumentOrganizationView.vue'
import BaseDataWarehouseView from '../views/base-data/BaseDataWarehouseView.vue'
import DictionaryManagementView from '../views/base-data/DictionaryManagementView.vue'
import DocumentTypeManagementView from '../views/base-data/DocumentTypeManagementView.vue'
import BorrowingView from '../views/borrowing/BorrowingView.vue'
import DashboardView from '../views/dashboard/DashboardView.vue'
import GovernanceView from '../views/governance/GovernanceView.vue'
import WorkflowManagementView from '../views/workflow/WorkflowManagementView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: ConsoleLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: DashboardView,
          meta: {
            title: '首页 / 档案智能工作台',
            breadcrumb: ['首页']
          }
        },
        {          path: 'base-data/warehouse',          name: 'base-data-warehouse',          component: BaseDataWarehouseView,          meta: {            title: '库房管理',            breadcrumb: ['配置中心', '库房管理']          }        },        {          path: 'base-data/document-types',          name: 'base-data-document-types',          component: DocumentTypeManagementView,          meta: {            title: '文档类型管理',            breadcrumb: ['配置中心', '文档类型管理']          }        },        {          path: 'base-data/dictionaries',          name: 'base-data-dictionaries',          component: DictionaryManagementView,          meta: {            title: '字典管理',            breadcrumb: ['配置中心', '字典管理']          }        },        {          path: 'base-data/document-organizations',          name: 'base-data-document-organizations',          component: BaseDataDocumentOrganizationView,          meta: {            title: '文档组织管理',            breadcrumb: ['配置中心', '文档组织管理']          }        },        {          path: 'base-data/archive-flow-rules',          name: 'base-data-archive-flow-rules',          component: BaseDataArchiveFlowRuleView,          meta: {            title: '归档规则管理',            breadcrumb: ['配置中心', '归档规则管理']          }        },        {          path: 'base-data/company-projects',          name: 'base-data-company-project-list',          component: BaseDataCompanyProjectListView,          meta: {            title: '公司/项目管理',            breadcrumb: ['配置中心', '公司/项目管理']          }        },        {          path: 'base-data/company-projects/create',          name: 'base-data-company-project-create',          component: BaseDataCompanyProjectDetailView,          meta: {            title: '新建公司/项目',            breadcrumb: ['配置中心', '公司/项目管理', '新建']          }        },        {          path: 'base-data/company-projects/:companyProjectCode/view',          name: 'base-data-company-project-view',          component: BaseDataCompanyProjectDetailView,          meta: {            title: '查看公司/项目',            breadcrumb: ['配置中心', '公司/项目管理', '查看']          }        },        {          path: 'base-data/company-projects/:companyProjectCode/edit',          name: 'base-data-company-project-edit',          component: BaseDataCompanyProjectDetailView,          meta: {            title: '编辑公司/项目',            breadcrumb: ['配置中心', '公司/项目管理', '编辑']          }        },        {          path: 'base-data/company-project-dictionaries',          name: 'base-data-company-project-dictionaries',          component: BaseDataCompanyProjectDictionaryView,          meta: {            title: '公司/项目字典管理',            breadcrumb: ['配置中心', '公司/项目字典管理']          }        },
        {
          path: 'archive-management/create',
          name: 'archive-management-create',
          component: CreateArchiveView,
          meta: {
            title: '发起归档',
            breadcrumb: ['档案业务管理', '发起归档']
          }
        },
        {
          path: 'archive-management/ai-search',
          name: 'archive-management-ai-search',
          component: AiSearchResultsView,
          meta: {
            title: 'AI 搜索结果页',
            breadcrumb: ['档案业务管理', 'AI+档案']
          }
        },
        {
          path: 'archive-management/query',
          name: 'archive-management-query',
          component: ArchiveQueryView,
          meta: {
            title: '档案查询',
            breadcrumb: ['档案业务管理', '档案查询']
          }
        },
        {
          path: 'archive-management/detail/:id',
          name: 'archive-management-detail',
          component: ArchiveDetailView,
          meta: {
            title: '档案详情',
            breadcrumb: ['档案业务管理', '档案查询', '档案详情']
          }
        },
        {
          path: 'archive-management/transfer',
          name: 'archive-management-transfer',
          component: () => import('../views/receiving/TransferManagementView.vue'),
          meta: {
            title: '文档移交管理',
            breadcrumb: ['档案业务管理', '文档移交管理']
          }
        },
        {
          path: 'archive-management/transfer-query',
          name: 'archive-management-transfer-query',
          component: () => import('../views/receiving/TransferQueryView.vue'),
          meta: {
            title: '移交记录查询',
            breadcrumb: ['档案业务管理', '移交记录查询']
          }
        },
        {
          path: 'archive-management/borrow',
          name: 'archive-management-borrow',
          component: BorrowingView,
          meta: {
            title: '借阅文档',
            breadcrumb: ['档案业务管理', '借阅文档']
          }
        },
        {
          path: 'archive-management/bind',
          name: 'archive-management-bind',
          component: () => import('../views/archive-management/BindArchiveView.vue'),
          meta: {
            title: '成册整理',
            breadcrumb: ['档案业务管理', '成册整理'],
            description: '成册整理将衔接归档完成后的后续业务处理。'
          }
        },
        {
          path: 'archive-management/storage',
          name: 'archive-management-storage',
          component: () => import('../views/archive-management/StorageManagementView.vue'),
          meta: {
            title: '入库上架',
            breadcrumb: ['档案业务管理', '入库上架'],
            description: '入库上架页面将承接实体档案入库、上架和库位绑定。'
          }
        },
        {
          path: 'governance',
          name: 'governance',
          component: GovernanceView,
          meta: {
            title: '流程规则与 AI 治理',
            breadcrumb: ['平台治理', '流程规则与 AI 治理']
          }
        },
        {
          path: 'workflow',
          name: 'workflow',
          component: WorkflowManagementView,
          meta: {
            title: '工作流管理',
            breadcrumb: ['平台治理', '工作流管理']
          }
        }
      ]
    }
  ]
})

export default router
