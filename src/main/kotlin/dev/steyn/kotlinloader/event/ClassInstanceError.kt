package dev.steyn.kotlinloader.event

/**
 * Thrown when an instance of an Illegal class is created
 */
class ClassInstanceError(msg: String) : Error(msg)