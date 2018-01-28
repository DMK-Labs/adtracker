(ns trendtracker.ui.components.pure.form-inputs
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [trendtracker.forms.validators :as validators]))

(defn controlled-input [{:keys [form-state helpers placeholder label attr input-type]}]
  (let [{:keys [on-change on-blur]} helpers
        errors (get-in (forms-helpers/attr-errors form-state attr)
                       [:$errors$ :failed])]
    [ant/form-item {:label label
                    :validateStatus (when (not-empty errors) "error")
                    :help (validators/get-validator-message (first errors))
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}}
     [ant/input
      {:placeholder placeholder
       :onChange (on-change attr)
       :onBlur (on-blur attr)
       :type (or input-type :text)
       :value (forms-helpers/attr-get-in form-state attr)}]]))

(defn controlled-textarea [{:keys [form-state helpers placeholder label attr rows default-value]}]
  (let [{:keys [on-change on-blur]} helpers
        errors (get-in (forms-helpers/attr-errors form-state attr)
                       [:$errors$ :failed])]
    [ant/form-item {:label label
                    ;; :has-feedback true
                    :validateStatus (when (not-empty errors) "error")
                    :help (validators/get-validator-message (first errors))
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}}
     [ant/input-text-area
      {:placeholder placeholder
       :rows (or rows 8)
       :onChange (on-change attr)
       :onBlur (on-blur attr)
       ;; :value (forms-helpers/attr-get-in form-state attr)
       :defaultValue default-value
       :style {:max-width 400}}]]))

(defn controlled-switch [{:keys [form-state helpers label attr default-checked]}]
  (let [{:keys [set-value set-value-without-immediate-validation]} helpers
        errors (get-in (forms-helpers/attr-errors form-state attr)
                       [:$errors$ :failed])]
    [ant/form-item {:label label
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}
                    :help (validators/get-validator-message (first errors))}
     [ant/switch {:checked (forms-helpers/attr-get-in form-state attr)
                  :onChange #(set-value attr %)}]]))

(defn controlled-tree-select [{:keys [form-state helpers label attr treeCheckable
                                      treeDefaultExpandAll treeData placeholder]}]
  (let [{:keys [set-value]} helpers
        errors (get-in (forms-helpers/attr-errors form-state attr)
                       [:$errors$ :failed])]
    [ant/form-item {:label label
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}
                    :validateStatus (when (not-empty errors) "error")
                    :help (validators/get-validator-message (first errors))}
     [ant/tree-select
      {:treeCheckable treeCheckable
       :treeDefaultExpandAll treeDefaultExpandAll
       :treeData treeData
       :placeholder placeholder
       :value (forms-helpers/attr-get-in form-state attr)
       :onChange #(set-value attr %)
       :style {:max-width 400}}]]))

(defn controlled-radio-group [{:keys [form-state helpers label attr options]}]
  (let [{:keys [on-change]} helpers
        errors (get-in (forms-helpers/attr-errors form-state attr)
                       [:$errors$ :failed])]
    [ant/form-item {:label label
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}
                    :help (validators/get-validator-message (first errors))}
     [ant/radio-group
      {:options options
       :onChange (on-change attr)
       :value (forms-helpers/attr-get-in form-state attr)}]]))
