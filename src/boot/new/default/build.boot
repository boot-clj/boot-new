(def project '{{raw-name}})
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.7.0"]])

(task-options!
 pom {:project
      :version
      :description "FIXME: write description"
      :url         "http://example/FIXME"
      :scm         {:url "https://github.com/yourname/{{name}}"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))
