-- :name upsert-campaign :! :n
INSERT INTO naver.campaign
(customer_id, id, name, campaign_type_id, delivery_method_id, is_using_period, period_starts_at, period_ends_at, reg_at, del_at)
VALUES
  (:customer_id, :id, :name, :campaign_type_id, :delivery_method_id, :is_using_period, :period_starts_at,
   :period_ends_at, :reg_at, :del_at)
ON CONFLICT (id)
  DO UPDATE SET
    (customer_id, name, campaign_type_id, delivery_method_id, is_using_period, period_starts_at, period_ends_at, reg_at,
     del_at)
    =
    (:customer_id, :name, :campaign_type_id, :delivery_method_id, :is_using_period, :period_starts_at, :period_ends_at,
     :reg_at, :del_at);

-- :name upsert-business-channel :! :n
INSERT INTO naver.business_channel
(customer_id, name, id, business_channel_type_id, channel_contents, pc_inspect_status_id, mobile_inspect_status_id, reg_at, del_at)
VALUES
  (:customer_id, :name, :id, :business_channel_type_id, :channel_contents, :pc_inspect_status_id,
   :mobile_inspect_status_id, :reg_at, :del_at)
ON CONFLICT (id)
  DO UPDATE SET
    (customer_id, name, business_channel_type_id, channel_contents, pc_inspect_status_id, mobile_inspect_status_id, reg_at, del_at)
    =
    (:customer_id, :name, :business_channel_type_id, :channel_contents, :pc_inspect_status_id,
     :mobile_inspect_status_id, :reg_at, :del_at)

-- :name upsert-adgroup :! :n
INSERT INTO naver.ad_group
(customer_id, id, campaign_id, name, bid, is_off, is_using_contents_network_bid, contents_network_bid, pc_network_bid_weight, mobile_network_bid_weight, keyword_plus, keyword_plus_bid_weight, pc_business_channel_id, mobile_business_channel_id, reg_at, del_at, content_type)
VALUES (:customer_id, :id, :campaign_id, :name, :bid, :is_off, :is_using_contents_network_bid, :contents_network_bid,
                      :pc_network_bid_weight, :mobile_network_bid_weight, :keyword_plus, :keyword_plus_bid_weight,
        :pc_business_channel_id, :mobile_business_channel_id, :reg_at, :del_at, :content_type)
ON CONFLICT (id)
  DO UPDATE SET
    (customer_id, campaign_id, name, bid, is_off, is_using_contents_network_bid, contents_network_bid, pc_network_bid_weight, mobile_network_bid_weight, keyword_plus, keyword_plus_bid_weight, pc_business_channel_id, mobile_business_channel_id, reg_at, del_at, content_type)
    =
    (:customer_id, :campaign_id, :name, :bid, :is_off, :is_using_contents_network_bid, :contents_network_bid, :pc_network_bid_weight, :mobile_network_bid_weight, :keyword_plus, :keyword_plus_bid_weight, :pc_business_channel_id, :mobile_business_channel_id, :reg_at, :del_at, :content_type);

-- :name upsert-keyword :! :n
INSERT INTO naver.keyword (customer_id, ad_group_id, id, keyword, bid, pc_landing_url, mobile_landing_url, is_off, inspect_status_id, is_using_ad_group_bid, reg_at, del_at)
VALUES
  (:customer_id, :ad_group_id, :id, :keyword, :bid, :pc_landing_url, :mobile_landing_url, :is_off, :inspect_status_id,
                 :is_using_ad_group_bid, :reg_at, :del_at)
ON CONFLICT (id)
  DO UPDATE SET
    (customer_id, ad_group_id, keyword, bid, pc_landing_url, mobile_landing_url, is_off, inspect_status_id, is_using_ad_group_bid, reg_at, del_at)
    =
    (:customer_id, :ad_group_id, :keyword, :bid, :pc_landing_url, :mobile_landing_url, :is_off, :inspect_status_id,
      :is_using_ad_group_bid, :reg_at, :del_at);

-- :name upsert-ad :! :n
INSERT INTO naver.ad (customer_id, ad_group_id, id, inspect_status_id, subject, description, pc_landing_url, mobile_landing_url, is_off, reg_at, del_at)
VALUES
  (:customer_id, :ad_group_id, :id, :inspect_status_id, :subject, :description, :pc_landing_url, :mobile_landing_url,
    :is_off, :reg_at, :del_at)
ON CONFLICT (id)
  DO UPDATE SET
    (customer_id, ad_group_id, id, inspect_status_id, subject, description, pc_landing_url, mobile_landing_url, is_off, reg_at, del_at)
    =
    (:customer_id, :ad_group_id, :id, :inspect_status_id, :subject, :description, :pc_landing_url, :mobile_landing_url, :is_off, :reg_at, :del_at)

-- :name upsert-qi :! :n
INSERT INTO naver.quality_index (customer_id, ad_group_id, keyword_id, keyword, quality_index)
VALUES (:customer_id, :ad_group_id, :keyword_id, :keyword, :quality_index)
ON CONFLICT (keyword_id)
  DO UPDATE SET
    (customer_id, ad_group_id, keyword_id, keyword, quality_index) = (:customer_id, :ad_group_id, :keyword_id, :keyword, :quality_index)


