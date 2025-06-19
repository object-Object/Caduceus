(ns gay.object.caduceus.registry
  (:require [gay.object.caduceus.core :as caduceus]
            [gay.object.caduceus.casting.actions.delimcc :as actions.delimcc]
            [gay.object.caduceus.casting.arithmetic :as arithmetic]
            [gay.object.caduceus.casting.eval.vm.frames :as frames])
  (:import (at.petrak.hexcasting.api.casting ActionRegistryEntry)
           (at.petrak.hexcasting.api.casting.math HexDir HexPattern)
           (at.petrak.hexcasting.common.lib HexRegistries)
           (at.petrak.hexcasting.common.lib.hex HexActions HexArithmetics HexContinuationTypes)))

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
                     (HexPattern/fromAngles signature start-dir)
                     action)))

(def actions
  (->Registrar
    (fn [] HexRegistries/ACTION)
    (fn [] HexActions/REGISTRY)
    {:prompt (make-action "eval/prompt" HexDir/EAST "wdeaqe" actions.delimcc/op-prompt)
     :control (make-action "eval/control" HexDir/WEST "waqdeq" actions.delimcc/op-control)}))

(def arithmetics
  (->Registrar
    (fn [] HexRegistries/ARITHMETIC)
    (fn [] HexArithmetics/REGISTRY)
    {:continuation (make-lazy-entry "continuation" arithmetic/continuation-arithmetic)}))

(def continuation-types
  (->Registrar
    (fn [] HexRegistries/CONTINUATION_TYPE)
    (fn [] HexContinuationTypes/REGISTRY)
    {:prompt (make-entry "prompt" frames/prompt-frame-type)}))
