package com.sychev.facedetector.domain

import javax.annotation.Nullable

interface DomainMapper<DomainModel, T> {

//    fun fromDomainModel(model: DomainModel): T

    fun toDomainModel(model: T): DomainModel

}