(ns dumb-react-three-webvr-example-online.app
  (:require-macros [dumb-react-three-webvr-example-online.macros :refer [+=]])
  (:import [goog.ui IdGenerator])
  (:require [goog.debug :as debug]))

(enable-console-print!)

(defn grab-nested-attr [obj attr-name]
  (aget obj attr-name))

(def window (partial grab-nested-attr js/window))

(def ^:dynamic *component* nil)

(defn random-radian []
  (* (js/Math.random) js/Math.PI))

(defn random-position [size]
  (* size (- (js/Math.random) 0.5)))

(defn guid []
  (.getNextUniqueId (.getInstance IdGenerator)))

(def create-class js/React.createClass)
(def create-element js/React.createElement)
(def obj-3d js/ReactTHREE.Object3D)
(def mesh-factory (js/React.createFactory js/ReactTHREE.Mesh))
(def box-geometry (js/THREE.BoxGeometry. 200 200 200))
(def mesh-material (js/THREE.MeshBasicMaterial. #js {:color "red"}))


(def Cupcake (create-class #js {:displayName "Cupcake"
                                :render (fn []
                                          (this-as this
                                                   (create-element
                                                    obj-3d
                                                    #js {:quaternion (aget this "props" "quaternion")
                                                         :position (or (aget  this "props" "position") (js/THREE.Vector3. 0 0 0))}
                                                    (mesh-factory #js {:position (js/THREE.Vector3. 0 -100 0)
                                                                       :geometry box-geometry
                                                                       :material mesh-material
                                                                       })
                                                    (mesh-factory #js {:position (js/THREE.Vector3. 0 100 0)
                                                                       :geometry box-geometry
                                                                       :material mesh-material
                                                                       })
                                                    )))}))


(def ExampleScene (create-class #js {:displayName "ExampleScene"
                                     :render (fn []
                                               (this-as this
                                                        (let [width (aget this "props" "width")
                                                              height (aget this "props" "height")
                                                              aspect (/ width height)
                                                              position (js/THREE.Vector3. 0 0 600)
                                                              lookat (js/THREE.Vector3. 0 0 0)
                                                              main-camera-element (create-element
                                                                                   js/ReactTHREE.PerspectiveCamera
                                                                                   #js {:name "maincamera"
                                                                                        :fov "75"
                                                                                        :aspect aspect
                                                                                        :near 1
                                                                                        :far 5000
                                                                                        :position position
                                                                                        :lookat lookat})]
                                                          (create-element
                                                           js/ReactTHREE.Scene
                                                           #js {:width width
                                                                :height height
                                                                :camera "maincamera"}
                                                           main-camera-element
                                                           (create-element Cupcake (aget this "props" "cupcakedata"))
                                                           ))))}))



(defn init []
  (let [render-element (.. js/document (getElementById "three-box"))
        height (window "innerHeight")
        width (window "innerWidth")
        position (js/THREE.Vector3. 0 0 0)
        quaternion (js/THREE.Quaternion.)
        scene-props #js {:width width
                         :height height
                         :cupcakedata #js {:position position
                                           :quaternion quaternion}}
        cupcakeprops (aget scene-props "cupcakedata")
        rotationangle 0
        scene (create-element ExampleScene scene-props)
        react-instance (js/React.render scene render-element)
        spincupcake (fn spincupcake [t]
                      (let [rotation-angle (* t 0.001)
                            euler (js/THREE.Euler. rotation-angle
                                                   (* rotation-angle 3)
                                                   0)
                            quaternion (aget scene-props "cupcakedata" "quaternion")]
                        (.. quaternion (setFromEuler euler))
                        (aset cupcakeprops "position" "x" (* 300 (js/Math.sin rotation-angle)))
                        (.. react-instance (setProps scene-props))
                        (js/requestAnimationFrame spincupcake)
                        ))]
    (spincupcake 1)
    ))
