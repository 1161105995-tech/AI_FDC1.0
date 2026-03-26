import { createRouter, createWebHistory } from 'vue-router'
import ConsoleLayout from '../layouts/ConsoleLayout.vue'
import CreateArchiveView from '../views/archive-management/CreateArchiveView.vue'
import AiSearchResultsView from '../views/archive-management/AiSearchResultsView.vue'
import ArchiveQueryView from '../views/archive-management/ArchiveQueryView.vue'
import PlaceholderPage from '../views/PlaceholderPage.vue'
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
        {
          path: 'base-data/warehouse',
          name: 'base-data-warehouse',
          component: BaseDataWarehouseView,
          meta: {
            title: '库房管理',
            breadcrumb: ['基础数据维护', '库房管理']
          }
        },
        {
          path: 'base-data/document-types',
          name: 'base-data-document-types',
          component: DocumentTypeManagementView,
          meta: {
            title: '档案类型管理',
            breadcrumb: ['基础数据维护', '档案类型管理']
          }
        },
        {
          path: 'base-data/dictionaries',
          name: 'base-data-dictionaries',
          component: DictionaryManagementView,
          meta: {
            title: '字典管理',
            breadcrumb: ['基础数据维护', '字典管理']
          }
        },
        {
          path: 'base-data/document-organizations',
          name: 'base-data-document-organizations',
          component: BaseDataDocumentOrganizationView,
          meta: {
            title: '档案组织管理',
            breadcrumb: ['基础数据维护', '档案组织管理']
          }
        },
        {
          path: 'base-data/archive-flow-rules',
          name: 'base-data-archive-flow-rules',
          component: BaseDataArchiveFlowRuleView,
          meta: {
            title: '归档规则管理',
            breadcrumb: ['基础数据维护', '归档规则管理']
          }
        },
        {
          path: 'base-data/company-projects',
          name: 'base-data-company-project-list',
          component: BaseDataCompanyProjectListView,
          meta: {
            title: '公司/项目管理',
            breadcrumb: ['基础数据维护', '公司/项目管理']
          }
        },
        {
          path: 'base-data/company-projects/create',
          name: 'base-data-company-project-create',
          component: BaseDataCompanyProjectDetailView,
          meta: {
            title: '新建公司/项目',
            breadcrumb: ['基础数据维护', '公司/项目管理', '新建']
          }
        },
        {
          path: 'base-data/company-projects/:companyProjectCode/view',
          name: 'base-data-company-project-view',
          component: BaseDataCompanyProjectDetailView,
          meta: {
            title: '查看公司/项目',
            breadcrumb: ['基础数据维护', '公司/项目管理', '查看']
          }
        },
        {
          path: 'base-data/company-projects/:companyProjectCode/edit',
          name: 'base-data-company-project-edit',
          component: BaseDataCompanyProjectDetailView,
          meta: {
            title: '编辑公司/项目',
            breadcrumb: ['基础数据维护', '公司/项目管理', '编辑']
          }
        },
        {
          path: 'base-data/company-project-dictionaries',
          name: 'base-data-company-project-dictionaries',
          component: BaseDataCompanyProjectDictionaryView,
          meta: {
            title: '公司/项目字典管理',
            breadcrumb: ['基础数据维护', '公司/项目字典管理']
          }
        },
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
          path: 'archive-management/transfer',
          name: 'archive-management-transfer',
          component: PlaceholderPage,
          meta: {
            title: '发起移交',
            breadcrumb: ['档案业务管理', '发起移交'],
            description: '移交流程页面将在下一阶段补齐，当前可先从首页或业务入口进入流程占位页。'
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
          component: PlaceholderPage,
          meta: {
            title: '成册整理',
            breadcrumb: ['档案业务管理', '成册整理'],
            description: '成册整理将衔接归档完成后的后续业务处理。'
          }
        },
        {
          path: 'archive-management/storage',
          name: 'archive-management-storage',
          component: PlaceholderPage,
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
        }
      ]
    }
  ]
})

export default router
