/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wwxd.utils.photo

import android.annotation.TargetApi
import android.content.Context
import android.widget.OverScroller
import android.widget.Scroller

abstract class ScrollerProxy {
    abstract fun computeScrollOffset(): Boolean
    abstract fun fling(
        startX: Int, startY: Int, velocityX: Int, velocityY: Int, minX: Int, maxX: Int, minY: Int,
        maxY: Int, overX: Int, overY: Int
    )

    abstract fun forceFinished(finished: Boolean)
    abstract val currX: Int
    abstract val currY: Int

    @TargetApi(9)
    private class GingerScroller(context: Context?) : ScrollerProxy() {
        private val mScroller: OverScroller
        override fun computeScrollOffset(): Boolean {
            return mScroller.computeScrollOffset()
        }

        override fun fling(
            startX: Int,
            startY: Int,
            velocityX: Int,
            velocityY: Int,
            minX: Int,
            maxX: Int,
            minY: Int,
            maxY: Int,
            overX: Int,
            overY: Int
        ) {
            mScroller.fling(
                startX,
                startY,
                velocityX,
                velocityY,
                minX,
                maxX,
                minY,
                maxY,
                overX,
                overY
            )
        }

        override fun forceFinished(finished: Boolean) {
            mScroller.forceFinished(finished)
        }

        override val currX: Int
            get() = mScroller.currX
        override val currY: Int
            get() = mScroller.currY

        init {
            mScroller = OverScroller(context)
        }
    }

    private class PreGingerScroller private constructor(context: Context) : ScrollerProxy() {
        private val mScroller: Scroller
        override fun computeScrollOffset(): Boolean {
            return mScroller.computeScrollOffset()
        }

        override fun fling(
            startX: Int,
            startY: Int,
            velocityX: Int,
            velocityY: Int,
            minX: Int,
            maxX: Int,
            minY: Int,
            maxY: Int,
            overX: Int,
            overY: Int
        ) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
        }

        override fun forceFinished(finished: Boolean) {
            mScroller.forceFinished(finished)
        }

        override val currX: Int
            get() = mScroller.currX
        override val currY: Int
            get() = mScroller.currY

        init {
            mScroller = Scroller(context)
        }
    }

    companion object {
        fun getScroller(context: Context?): ScrollerProxy {
            return GingerScroller(context)
        }
    }
}