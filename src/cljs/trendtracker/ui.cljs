(ns trendtracker.ui
  (:require [trendtracker.ui.components.tabbed-charts :as tabbed-charts]
            [trendtracker.ui.components.breadcrumbs :as breadcrumbs]
            [trendtracker.ui.components.counter :as counter]
            [trendtracker.ui.components.date-range :as date-range]
            [trendtracker.ui.components.footer :as footer]
            [trendtracker.ui.components.header :as header]
            [trendtracker.ui.components.snapshot :as snapshot]
            [trendtracker.ui.components.sider :as sider]
            [trendtracker.ui.pages.dashboard :as dashboard]
            [trendtracker.ui.pages.keyword-tool :as keyword-tool]
            [trendtracker.ui.pages.login :as login]
            [trendtracker.ui.pages.optimize :as optimize]
            [trendtracker.ui.pages.user :as user]
            [trendtracker.ui.root :as root]))

(def ui
  {:main              root/component
   :header            header/component
   :sider             sider/component
   :date-range-picker date-range/component
   :footer            footer/component
   :login-page        login/component
   :user-page         user/component
   :dashboard-page    dashboard/component
   :keyword-tool-page keyword-tool/component
   :optimize-page     optimize/component
   :counter           counter/component
   :breadcrumbs       breadcrumbs/component
   :snapshot          snapshot/component
   :tabbed-charts     tabbed-charts/component})
