(ns boot.generate.edn
  (:require [boot.new.templates :as tmpl]))

(defn generate
  "Generate a new edn file relative to the prefix."
  [prefix ns-name & [body ext]]
  (gen-file/generate prefix ns-name (str body) (or ext "edn")))
