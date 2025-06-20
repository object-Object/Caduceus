(ns gay.object.caduceus.init
  (:require [gay.object.caduceus.core :as caduceus]
            [gay.object.caduceus.registry :as registry]))

(defn init [init-registry]
  (.info caduceus/LOGGER "Making Iris' Gambit even more confusing...")
  (doseq [registrar registry/registrars]
    (init-registry registrar)))

(defn init-client [])
