package com.example.surgeryapptest.utils.network

import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.AssignedPatientsList
import com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse.WoundImageFeedback
import com.example.surgeryapptest.model.network.doctorResponse.sendFeedbackResponse.SendWoundFeedbackResponse
import com.example.surgeryapptest.model.network.patientResponse.deleteEntryNetworkResponse.NetworkDeleteEntryResponse
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.patientResponse.updateWoundImageResponse.NetworkUpdateEntryResponse
import com.example.surgeryapptest.model.network.patientResponse.uploadNewImageResponse.NetworkUploadNewEntryResponse
import com.example.surgeryapptest.model.network.updateDetails.UpdateDetailResponse
import com.example.surgeryapptest.model.network.userNetworkResponse.UserLoginNetworkResponse
import com.example.surgeryapptest.model.network.utilsResponse.GeneralInfoResponse
import com.example.surgeryapptest.utils.network.endpoints.ApiInterface
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Part
import retrofit2.http.Path
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiInterface: ApiInterface
) {

    suspend fun getAllProgressEntry(userId: String): Response<AllProgressBookEntry> {
        return apiInterface.getAllProgressEntry(userId)
    }

    suspend fun getAllArchivedEntry(userId: String): Response<AllProgressBookEntry> {
        return apiInterface.getAllArchivedEntry(userId)
    }

    suspend fun uploadNewEntry(
        @Part("masterUserId_fk") userID: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("fluid_drain") fluid_drain: RequestBody,
        @Part("painrate") painrate: RequestBody,
        @Part("redness") redness: RequestBody,
        @Part("swelling") swelling: RequestBody,
        @Part("odour") odour: RequestBody,
        @Part("fever") fever: RequestBody,
    ): Response<NetworkUploadNewEntryResponse> {
        return apiInterface.uploadNewEntry(
            userID, image, title, description, fluid_drain, painrate,
            redness, swelling, odour, fever
        )
    }

    suspend fun updateUploadedEntry(
        @Part("entryID") entryID: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("fluid_drain") fluid_drain: RequestBody,
        @Part("painrate") painrate: RequestBody,
        @Part("redness") redness: RequestBody,
        @Part("swelling") swelling: RequestBody,
        @Part("odour") odour: RequestBody,
        @Part("fever") fever: RequestBody,
    ): Response<NetworkUpdateEntryResponse> {
        return apiInterface.updateUploadedEntry(
            entryID, title, description, fluid_drain, painrate,
            redness, swelling, odour, fever
        )
    }

    suspend fun deleteUploadedEntry(
        @Part("entryID") entryID: RequestBody
    ) : Response<NetworkDeleteEntryResponse> {
        return apiInterface.deleteUploadedEntry(entryID)
    }

    // Edited this to include prevFlag
    suspend fun archiveUploadedEntry(
        @Part("entryID") entryID: RequestBody,
        @Part("prevFlag") prevFlag: RequestBody,
    ) : Response<NetworkDeleteEntryResponse> {
        return apiInterface.archiveUploadedEntry(entryID, prevFlag)
    }

    // Authenticate the user
    suspend fun loginUser(@Body params: Map<String, String>):
            Response<UserLoginNetworkResponse> {
        return apiInterface.loginUser(params)
    }

    // Doctor Routes
    suspend fun getAssignedPatientsList(doctorId: String): Response<AssignedPatientsList> {
        return apiInterface.getAssignedPatientsList(doctorId)
    }

    // Researcher Routes
    suspend fun getAllPatientsList(): Response<AssignedPatientsList> {
        return  apiInterface.getAllPatientsList()
    }

    // Wound Feedback List
    suspend fun getFeedbackList(woundImageID: String): Response<WoundImageFeedback> {
        return apiInterface.getFeedbackList(woundImageID)
    }

    suspend fun sendFeedback(params: Map<String, String>):
            Response<SendWoundFeedbackResponse> {
        return apiInterface.sendFeedback(params)
    }

    suspend fun updatePhoneNumber(
        userContact1: RequestBody,
        userContact2: RequestBody,
        userID: RequestBody,
    ) : Response<UpdateDetailResponse> {
        return apiInterface.updatePhoneNumber(userContact1, userContact2, userID)
    }

    suspend fun getGeneralInfoList(): Response<GeneralInfoResponse> {
        return apiInterface.getGeneralInfoList()
    }

}