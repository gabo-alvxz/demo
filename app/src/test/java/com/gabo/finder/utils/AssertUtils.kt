package com.gabo.finder.utils

import java.lang.AssertionError
import kotlin.reflect.KClass

fun  assertInstance(
    expected : KClass<*>,
    actual : Any?
){
    if(!expected.isInstance(actual)){
        throw AssertionError("Got wrong instance. Expected : ${expected.java.name} , " +
                "got ${actual?.javaClass?.name}")
    }
}