(ns trendtracker.ui.components.pure.form-inputs
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [trendtracker.forms.validators :as validators]))

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
