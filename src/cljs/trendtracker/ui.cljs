(ns trendtracker.ui
  (:require [trendtracker.ui.components.breadcrumbs :as breadcrumbs]
            [trendtracker.ui.components.cascader :as cascader]
            [trendtracker.ui.components.date-range :as date-range]
            [trendtracker.ui.components.footer :as footer]
            [trendtracker.ui.components.header :as header]
            [trendtracker.ui.components.sider :as sider]
            [trendtracker.ui.components.snapshot :as snapshot]
            [trendtracker.ui.components.charts.tabbed-charts :as tabbed-charts]
            [trendtracker.ui.pages.dashboard :as dashboard]
            [trendtracker.ui.pages.keyword-tool :as keyword-tool]
            [trendtracker.ui.pages.login :as login]
            [trendtracker.ui.pages.optimize :as optimize]
            [trendtracker.ui.pages.optimize-new :as optimize-new]
            [trendtracker.ui.pages.overview :as overview]
            [trendtracker.ui.pages.user :as user]
            [trendtracker.ui.root :as root]
            [trendtracker.ui.components.optimize.budgeting :as budgeting]
            [trendtracker.ui.components.optimize.objective :as objective]
            [trendtracker.ui.components.optimize.detail :as detail]
            [trendtracker.ui.components.optimize.portfolio :as portfolio]
            [trendtracker.ui.components.charts.conversion-funnel :as conversion-funnel]
            [trendtracker.ui.components.keyword-tool.results-table :as results-table]
            [trendtracker.ui.pages.keywords :as keywords]
            [trendtracker.ui.components.optimize.slider :as slider]))

(def ui
  {
   ;; Layout
   :main        root/component
   :header      header/component
   :sider       sider/component
   :footer      footer/component
   :breadcrumbs breadcrumbs/component

   ;; Components
   :date-range-picker          date-range/component
   :snapshot                   snapshot/component
   :tabbed-charts              tabbed-charts/component
   :cascader                   cascader/component
   :conversion-funnel          conversion-funnel/component
   :budgeting                  budgeting/component
   :objective                  objective/component
   :detail                     detail/component
   :portfolio                  portfolio/component
   :keyword-tool-results-table results-table/component

   :slider slider/component

   ;; Pages
   :login-page        login/component
   :user-page         user/component
   :dashboard-page    dashboard/component
   :keyword-tool-page keyword-tool/component
   :optimize-page     optimize/component
   :optimize-new-page optimize-new/component
   :overview-page     overview/component
   :keywords-page     keywords/component})
