(ns komcrad-utils.nrepl-middleware
  (:require [nrepl.misc :refer [response-for]]
            [nrepl.transport :as t]
            [nrepl.middleware :as middle]
            [clipboard.core :as cb]))

(defn result->clipboard [{:keys [value] :as msg}]
  (when value
    (cb/spit (str value)))
  msg)

(defn wrap-clipboard
  [handler]
  (fn [{:keys [op transport] :as msg}]
    (handler (assoc msg :transport
                    (reify t/Transport
                      (recv [this] (t/recv transport))
                      (recv [this timeout] (t/recv transport))
                      (send [this msg] (t/send transport (result->clipboard msg))))))))

(middle/set-descriptor! #'wrap-clipboard
                        {:expects #{"eval"}
                         :requires #{}
                         :handles {}})
