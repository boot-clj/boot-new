(ns boot.new.task
  (:require [boot.new.templates :refer [renderer sanitize year date ->files]]))

(defn task
  "A Boot task template."
  [^String name]
  (let [render (renderer "task")
        unprefixed (if (.startsWith name "boot-")
                     (subs name 5)
                     name)
        data {:name name
              :unprefixed-name unprefixed
              :sanitized (sanitize unprefixed)
              :year (year)
              :date (date)}]
    (println (str "Generating a fresh Boot task called " name "."))
    (->files data
             ["build.boot" (render "build.boot" data)]
             ["README.md" (render "README.md" data)]
             [".gitignore" (render "gitignore" data)]
             [".hgignore" (render "hgignore" data)]
             ["src/boot/{{sanitized}}.clj" (render "name.clj" data)]
             ["LICENSE" (render "LICENSE" data)]
             ["CHANGELOG.md" (render "CHANGELOG.md" data)])))
