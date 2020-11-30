/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wwxd.protractor.camera

import android.os.IBinder
import java.lang.reflect.Method

/**
 * This class is used to activate the weak light on some camera phones (not
 * flash) in order to illuminate surfaces for scanning. There is no official way
 * to do this, but, classes which allow access to this function still exist on
 * some devices. This therefore proceeds through a great deal of reflection.
 *
 *
 * See [ http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-
 * programatically/](http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-programatically/) and [ http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo
 * /DroidLED.java](http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo/DroidLED.java). Thanks to Ryan Alford for pointing out the availability
 * of this class.
 */
internal class FlashlightManager {
    private var hardwareService: Any? = null
        get() {
            if (field == null) {
                val serviceManagerClass = maybeForName("android.os.ServiceManager") ?: return null
                val getServiceMethod = maybeGetMethod(
                    serviceManagerClass,
                    "getService", String::class.java
                ) ?: return null
                val hardwareService1 = invoke(getServiceMethod, null, "hardware") ?: return null
                val iHardwareServiceStubClass =
                    maybeForName("android.os.IHardwareService\$Stub") ?: return null
                val asInterfaceMethod = maybeGetMethod(
                    iHardwareServiceStubClass,
                    "asInterface", IBinder::class.java
                ) ?: return null
                field = invoke(asInterfaceMethod, null, hardwareService1)
            }
            return field
        }
    private var flashEnabledMethod: Method? = null
        get() {
            if (field == null) {
                if (hardwareService == null) {
                    return null
                }
                val proxyClass: Class<*> = hardwareService!!.javaClass
                field = maybeGetMethod(
                    proxyClass,
                    "setFlashlightEnabled",
                    Boolean::class.javaPrimitiveType!!
                )
            }
            return field
        }

    private fun maybeForName(name: String): Class<*>? {
        return try {
            Class.forName(name)
        } catch (cnfe: Exception) {
            null
        }
    }

    private fun maybeGetMethod(
        clazz: Class<*>, name: String,
        vararg argClasses: Class<*>
    ): Method? {
        return try {
            clazz.getMethod(name, *argClasses)
        } catch (re: Exception) {
            null
        }
    }

    private operator fun invoke(method: Method?, instance: Any?, vararg args: Any): Any? {
        return try {
            method!!.invoke(instance, *args)
        } catch (e: Exception) {
            null
        }
    }

      fun enableFlashlight() {
        setFlashlight(true)
    }

      fun disableFlashlight() {
        setFlashlight(false)
    }

    private fun setFlashlight(active: Boolean) {
        if (hardwareService != null) {
            try {
                invoke(flashEnabledMethod, hardwareService, active)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}