package com.hoppipotame.discord.domain.port.outBound

import com.hoppipotame.discord.domain.model.HashQuery
import com.hoppipotame.discord.domain.model.MagnetQuery

interface DemagnetizePort {
    fun fromHash(hashQuery: HashQuery)
    fun fromMagnet(magnetQuery: MagnetQuery)
}