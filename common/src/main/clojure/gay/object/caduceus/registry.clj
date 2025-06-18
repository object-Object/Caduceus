(ns gay.object.caduceus.registry
  (:require [gay.object.caduceus.core :as caduceus])
  (:import (at.petrak.hexcasting.api.casting ActionRegistryEntry)
           (at.petrak.hexcasting.api.casting.math HexPattern)
           (at.petrak.hexcasting.common.lib HexRegistries)
           (at.petrak.hexcasting.common.lib.hex HexActions HexArithmetics)
           (gay.object.caduceus.casting.arithmetic ContinuationArithmetic)))

(defrecord Registrar [get-registry-key get-registry entries])

(defn init [registrar registerer]
  (dosync
    (doseq [entry (vals (:entries registrar))
            :let [{id :id
                   value-ref :value-ref} entry]]
      (as-> value-ref value
            (alter value #(%))
            (registerer id value)))))

(defn get-registry-key [registrar]
  ((:get-registry-key registrar)))

(defn get-registry [registrar]
  ((:get-registry registrar)))

(defrecord RegistrarEntry [id value-ref])

(defn- make-lazy-entry [name get-value]
  (->RegistrarEntry (caduceus/id name) (ref get-value)))

(defn- make-entry [name value]
  (make-lazy-entry name (fn [] value)))

(defn- make-action [name start-dir signature action]
  (make-entry name (ActionRegistryEntry/new
                     (HexPattern/fromAngles start-dir signature)
                     action)))

(def ACTIONS
  (->Registrar
    (fn [] HexRegistries/ACTION)
    (fn [] HexActions/REGISTRY)
    {}))

(def ARITHMETICS
  (->Registrar
    (fn [] HexRegistries/ARITHMETIC)
    (fn [] HexArithmetics/REGISTRY)
    {:continuation (make-entry "continuation" ContinuationArithmetic/INSTANCE)}))
