package com.sychev.facedetector.domain

interface DomainMapper<DomainModel, T> {

    fun fromDomainModel(model: DomainModel): T

    fun toDomainModel(model: T): DomainModel

}