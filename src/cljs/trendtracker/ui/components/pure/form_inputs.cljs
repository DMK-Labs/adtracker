(ns trendtracker.ui.components.pure.form-inputs
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [trendtracker.forms.validators :as validators]
            [keechma.toolbox.forms.ui :as forms-ui]
            [reagent.core :as r]))

(defn make-input-with-composition-support
  "This function implements input fields that handle composition based inputs correctly"
  [tag]
  (fn [props]
    (let [el-ref-atom (atom nil)
          composition-atom? (atom false)]
      (r/create-class
       {:reagent-render (fn [props]
                          (let [props-ref  (or (:ref props) identity)
                                props-on-change (or (:on-change props) identity)
                                props-value (:value props)
                                props-without-value (dissoc props :value)]
                            [tag (merge props-without-value
                                        {:on-change (fn [e]
                                                      (when-not @composition-atom?
                                                        (props-on-change e)))
                                         :on-composition-start #(reset! composition-atom? true)
                                         :on-composition-update #(reset! composition-atom? true)
                                         :on-composition-end (fn [e]
                                                               (reset! composition-atom? false)
                                                               (props-on-change e))
                                         :default-value props-value
                                         :ref (fn [el]
                                                (reset! el-ref-atom el)
                                                (props-ref el))})]))
        :component-will-update (fn [comp [_ new-props _]]
                                 (let [el @el-ref-atom
                                       composition? @composition-atom?]
                                   (when (and el (not composition?))
                                     (set! (.-value el) (or (:value new-props) "")))))}))))

(def input-with-composition-support (make-input-with-composition-support ant/input))
(def textarea-with-composition-support (make-input-with-composition-support ant/input-text-area))

(defn controlled-input [ctx form-props attr {:keys [placeholder label input-type]}]
  (let [errors (get-in (forms-ui/errors-in> ctx form-props attr) [:$errors$ :failed])]
    [ant/form-item {:label label
                    :validateStatus (when (not-empty errors) "error")
                    :help (validators/get-validator-message (first errors))
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}}
     [input-with-composition-support
      {:placeholder placeholder
       :on-change #(forms-ui/<on-change ctx form-props attr %)
       :on-blur #(forms-ui/<on-blur ctx form-props attr %)
       :type (or input-type :text)
       :value (forms-ui/value-in> ctx form-props attr)}]]))

(defn controlled-textarea [ctx form-props attr {:keys [placeholder label rows default-value]}]
  (let [errors (get-in (forms-ui/errors-in> ctx form-props attr) [:$errors$ :failed])]
    [ant/form-item {:label label
                    :validateStatus (when (not-empty errors) "error")
                    :help (validators/get-validator-message (first errors))
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}}
     [textarea-with-composition-support
      {:placeholder placeholder
       :rows (or rows 10)
       :on-change #(forms-ui/<on-change ctx form-props attr %)
       :on-blur #(forms-ui/<on-blur ctx form-props attr %)
       :value (forms-ui/value-in> ctx form-props attr)
       :default-value default-value
       :style {:max-width 420}}]]))

(defn controlled-switch [ctx form-props attr {:keys [label default-checked]}]
  (let [errors (get-in (forms-ui/errors-in> ctx form-props attr) [:$errors$ :failed])]
    [ant/form-item {:label label
                    :label-col {:xs {:span 24}
                                :sm {:span 6}}
                    :wrapper-col {:xs {:span 24}
                                  :sm {:span 18}}
                    :help (validators/get-validator-message (first errors))}
     [ant/switch {:checked (forms-ui/value-in> ctx form-props attr)
                  :default-checked default-checked
                  :on-change #(forms-ui/<set-value ctx form-props attr %)}]]))

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
       :onChange #(do (print %)
                      (set-value attr %))
       :style {:max-width 400}}]]))

(defn controlled-radio-group [ctx form-props attr {:keys [label options]}]
  (let [errors (get-in (forms-ui/errors-in> ctx form-props attr) [:$errors$ :failed])]
    [ant/form-item {:label label
                    :labelCol {:xs {:span 24}
                               :sm {:span 6}}
                    :wrapperCol {:xs {:span 24}
                                 :sm {:span 18}}
                    :help (validators/get-validator-message (first errors))}
     [ant/radio-group
      {:options options
       :on-change #(forms-ui/<on-change ctx form-props attr %)
       :value (forms-ui/value-in> ctx form-props attr)}]]))
