(ns trendtracker.ui.components.pure.form-inputs
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [trendtracker.forms.validators :as validators]))

(defn controlled-textarea [{:keys [form-state helpers placeholder label attr rows]}]
  (let [{:keys [on-change on-blur]} helpers]
    [ant/form-item {:label label
                    :has-feedback true
                    :help (validators/get-validator-message
                           (first (forms-helpers/attr-errors form-state attr)))
                    :label-col {:xs {:span 24}
                                :sm {:span 6}}
                    :wrapper-col {:xs {:span 24}
                                  :sm {:span 18}}}
     [ant/input-text-area
      {:placeholder placeholder
       :rows (or rows 8)
       :on-change (on-change attr)
       :on-blur (on-blur attr)
       :value (forms-helpers/attr-get-in form-state attr)
       :style {:max-width 400}}]]))
