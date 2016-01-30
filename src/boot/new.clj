(ns boot.new
  "Generate project scaffolding based on a template.
  Adapted from leiningen.new, with permission of the Leiningen team."
  {:boot/export-tasks true}
  (:refer-clojure :exclude [new])
  (:require [boot.core :as boot :refer [deftask]]
            [boot.new.templates :as bnt]
            [boot.util :as util]

            [bultitude.core :as bultitude]

            [leiningen.new.templates :as lnt])
  (:import java.io.FileNotFoundException))

(def ^:dynamic *use-snapshots?* false)
(def ^:dynamic *template-version* nil)

(defn resolve-remote-template
  "Given a template name, attempt to resolve it as a Boot template first,
  then as a Leiningen template. Return the type of template we found."
  [template-name]
  (let [selected (atom nil)
        failure  (atom nil)
        output
        (with-out-str
          (binding [*err* *out*]
            (try
              (boot/merge-env! :dependencies [[(symbol (str template-name "/boot-template"))
                                               (cond *template-version* *template-version*
                                                     *use-snapshots?*   "(0.0.0,)"
                                                     :else              "RELEASE")]])
              (reset! selected :boot)
              (catch Exception e
                (reset! failure e)
                (try
                  (boot/merge-env! :dependencies [[(symbol (str template-name "/lein-template"))
                                                   (cond *template-version* *template-version*
                                                         *use-snapshots?*   "(0.0.0,)"
                                                         :else              "RELEASE")]])
                  (reset! selected :leiningen)
                  ;; fetch Leiningen Core so template can use it:
                  (boot/merge-env! :dependencies '[[leiningen-core "2.5.3"]])
                  (catch Exception e
                    (reset! failure e)))))))]
    (if @selected
      (try
        (require (symbol (str (name @selected) ".new." template-name)))
        @selected
        (catch Exception e
          (util/exit-error (println "Could not load template, failed with:" (.getMessage e)))))
      (util/exit-error (println output)
                       (println "Could not load template, failed with:" (.getMessage @failure))))))

(defn resolve-template
  "Given a template name, resolve it to a symbol (or exit if not possible)."
  [template-name]
  (if-let [type (try (require (symbol (str "boot.new." template-name)))
                     :boot
                     (catch FileNotFoundException _
                       (resolve-remote-template template-name)))]
    (resolve (symbol (str (name type) ".new." template-name) template-name))
    (util/exit-error (println "Could not find template" template-name "on the classpath."))))

(defn create
  "Given a template name, a project name and list of template arguments,
  perform sanity checking on the project name and, if it's sane, then
  generate the project from the template."
  [template-name project-name args]
  (cond
    (and (re-find #"(?i)(?<!(clo|compo))jure" project-name)
         (not (System/getenv "BOOT_IRONIC_JURE")))
    (util/exit-error (println "Sorry, names such as clojure or *jure are not allowed."
                              "\nIf you intend to use this name ironically, please set the"
                              "\nBOOT_IRONIC_JURE environment variable and try again."))
    (and (re-find #"(?i)(?<!(cl|comp))eaxure" project-name)
         (not (System/getenv "BOOT_IRONIC_EAXURE")))
    (util/exit-error (println "Sorry, names such as cleaxure or *eaxure are not allowed."
                              "\nIf you intend to use this name ironically, please set the"
                              "\nBOOT_IRONIC_EAXURE environment variable and try again."))
    (= project-name "clojure")
    (util/exit-error (println "Sorry, clojure can't be used as a project name."
                              "\nIt will confuse Clojure compiler and cause obscure issues."))
    (and (re-find #"[A-Z]" project-name)
         (not (System/getenv "BOOT_BREAK_CONVENTION")))
    (util/exit-error (println "Project names containing uppercase letters are not recommended"
                              "\nand will be rejected by repositories like Clojars and Central."
                              "\nIf you're truly unable to use a lowercase name, please set the"
                              "\nBOOT_BREAK_CONVENTION environment variable and try again."))
    (not (symbol? (try (read-string project-name) (catch Exception _))))
    (util/exit-error (println "Project names must be valid Clojure symbols."))
    :else (apply (resolve-template template-name) project-name args)))

(defn template-show
  "Show details for a given template."
  [template-name]
  (let [resolved (meta (resolve-template template-name))]
    (println (:doc resolved "No documentation available."))
    (println)
    (println "Argument list:" (or (:help-arglists resolved)
                                  (:arglists resolved)))))

(ns-unmap *ns* 'new)

(deftask new
  "Generate a new project from a template. The long form arguments mostly
follow those of `lein new` except that -n / --name is required and you
specify the template with -t / --template."
  [a args             ARG      [str] "arguments for the template itself."
   f force                     bool  "Force Boot new to overwrite existing directory."
   n name             NAME     str   "generated project name"
   o to-dir           DIR      str   "directory to use instead of NAME"
   s show                      bool  "Show documentation for the template."
   S snapshot                  bool  "Look for a SNAPSHOT version of the template."
   t template         TEMPLATE str   "the template to use"
   V template-version VER      str   "the version of the template to use"]

  (let [template (or template "default")]

    (boot/with-pass-thru fs

      (cond show
            (template-show template)

            (not name)
            (util/exit-error (println "Project name is required (-n, --name)."))

            :else (binding [*use-snapshots?*   snapshot
                            *template-version* template-version
                            bnt/*dir*          to-dir
                            bnt/*force?*       force
                            lnt/*dir*          to-dir
                            lnt/*force?*       force]
                    (create template name args))))))
