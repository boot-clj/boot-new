(ns boot.generate.file
  (:require [boot.generate.file :as gen-file]
            [boot.new.templates :as tmpl]))

(defn generate
  "Generate a new file relative to the prefix."
  [prefix fs-name & [body ext append?]]
  (tmpl/->files {:prefix prefix :path (tmpl/name-to-path fs-name) :ext (or ext "clj")}
                ["{{prefix}}/{{path}}.{{ext}}" (str body) :append (or append? false)]))
