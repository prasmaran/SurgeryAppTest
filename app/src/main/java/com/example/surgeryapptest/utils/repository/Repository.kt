package com.example.surgeryapptest.utils.repository

import com.example.surgeryapptest.utils.network.RemoteDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource
){

    val remoteDataSource = remoteDataSource

}