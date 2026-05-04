package com.gianivander.captiongenerator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform