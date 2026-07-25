(ns gay.object.caduceus.registry
  (:require [gay.object.caduceus.core :as caduceus]
            [gay.object.caduceus.casting.actions.delimcc :as actions.delimcc]
            [gay.object.caduceus.casting.actions.go-to :as actions.go-to]
            [gay.object.caduceus.casting.actions.marks :as marks]
            [gay.object.caduceus.casting.arithmetic :as arithmetic]
            [gay.object.caduceus.casting.iota.delimcc :as iota.delimcc]
            [gay.object.caduceus.casting.eval.vm.frames :as frames])
  (:import (at.petrak.hexcasting.api.casting ActionRegistryEntry)
           (at.petrak.hexcasting.api.casting.math HexDir HexPattern)
           (at.petrak.hexcasting.common.lib HexRegistries)
           (at.petrak.hexcasting.common.lib.hex HexActions HexArithmetics HexContinuationTypes HexIotaTypes)))

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
    {:prompt (make-action "eval/prompt" HexDir/EAST "wdeaqe" (actions.delimcc/->OpPrompt))
     :prompt-at (make-action "eval/prompt_at" HexDir/NORTH_WEST "qedqaw" (actions.delimcc/->OpPromptAt))
     :control (make-action "eval/control" HexDir/WEST "waqdeq" (actions.delimcc/->OpControl))
     :control-at (make-action "eval/control_at" HexDir/NORTH_EAST "eqaedw" (actions.delimcc/->OpControlAt))
     :setup-goto (make-action "eval/setup_goto" HexDir/EAST "qqdqdqdqq" (actions.go-to/->OpSetupGoto))
     :goto (make-action "eval/goto" HexDir/NORTH_WEST "wdaawdaw" (actions.go-to/->OpGoto))
     :read-iota-mark (make-action "read/mark/iota" HexDir/EAST "adaaddad" (marks/->OpReadIotaMark))
     :read-local-mark (make-action "read/mark/local" HexDir/EAST "aeaaqawd" (marks/->OpReadLocalMark))
     :write-local-mark (make-action "write/mark/local" HexDir/WEST "dqddedwa" (marks/->OpWriteLocalMark))}))

(def arithmetics
  (->Registrar
    (fn [] HexRegistries/ARITHMETIC)
    (fn [] HexArithmetics/REGISTRY)
    {:continuation (make-lazy-entry "continuation" arithmetic/->ContinuationArithmetic)}))

(def continuation-types
  (->Registrar
    (fn [] HexRegistries/CONTINUATION_TYPE)
    (fn [] HexContinuationTypes/REGISTRY)
    {:goto (make-entry "goto" frames/goto-frame-type)
     :prompt (make-entry "prompt" frames/prompt-frame-type)}))

(def iota-types
  (->Registrar
    (fn [] HexRegistries/IOTA_TYPE)
    (fn [] HexIotaTypes/REGISTRY)
    {:delimited-continuation (make-entry "continuation/delimited" iota.delimcc/iota-type)}))

(def registrars [actions
                 arithmetics
                 continuation-types
                 iota-types])
