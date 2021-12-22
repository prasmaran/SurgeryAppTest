package com.example.surgeryapptest.utils.network.endpoints

import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.AssignedPatientsList
import com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse.WoundImageFeedback
import com.example.surgeryapptest.model.network.doctorResponse.sendFeedbackResponse.SendWoundFeedbackResponse
import com.example.surgeryapptest.model.network.patientResponse.deleteEntryNetworkResponse.NetworkDeleteEntryResponse
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.model.network.patientResponse.updateWoundImageResponse.NetworkUpdateEntryResponse
import com.example.surgeryapptest.model.network.patientResponse.uploadNewImageResponse.NetworkUploadNewEntryResponse
import com.example.surgeryapptest.model.network.pdfGenerationResponse.NetworkPDFGenerateResponse
import com.example.surgeryapptest.model.network.updateDetails.UpdateDetailResponse
import com.example.surgeryapptest.model.network.userNetworkResponse.UserLoginNetworkResponse
import com.example.surgeryapptest.model.network.utilsResponse.GeneralInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    /** ALL USERS ROUTES */

    // Authenticate the user
    @POST("/user/auth")
    suspend fun loginUser(@Body params: Map<String, String>):
            Response<UserLoginNetworkResponse>

    /** PATIENTS ROUTES */

    // To receive all the progress entry data
    // Edited: Get progress book by userID
    @GET("/books/progress/getAll/{userId}")
    suspend fun getAllProgressEntry(@Path("userId") userId: String): Response<AllProgressBookEntry>

    // To receive all the archived entries list
    // Edited: Get progress book by userID
    @GET("/books/progress/getAllArchived/{userId}")
    suspend fun getAllArchivedEntry(@Path("userId") userId: String): Response<AllProgressBookEntry>

    // To receive one progress entry by ID
    @GET("/testingnodeapp/books/progress/{entryid}")
    suspend fun getOneProgressEntry(): Response<AllProgressBookEntryItem>

    // To upload new wound image
    @Multipart
    @POST("/books/progress/upload")
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
    ): Response<NetworkUploadNewEntryResponse>

    // To edit selected image entry
    @Multipart
    @PUT("/books/progress/edit")
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
    ): Response<NetworkUpdateEntryResponse>

    // Delete selected image entry
    //@PUT("/books/progress/delete")
    /**
     * Delete only entries without feedback
     */
    @Multipart
    @PUT("/books/progress/deleteEntryNoFeedback")
    suspend fun deleteUploadedEntry(
        @Part("entryID") entryID: RequestBody
    ): Response<NetworkDeleteEntryResponse>

    // Archive selected image entry
    @Multipart
    @PUT("/books/progress/archive")
    suspend fun archiveUploadedEntry(
        @Part("entryID") entryID: RequestBody,
        @Part("prevFlag") prevFlag: RequestBody,
    ): Response<NetworkDeleteEntryResponse>

    /** DOCTOR ROUTES */

    // Retrieve assigned patients progress books
    @GET("/doctor/getAllPatients/{doctorId}")
    suspend fun getAssignedPatientsList(@Path("doctorId") doctorId: String): Response<AssignedPatientsList>

    // Retrieve specific wound image feedback
    @GET("/doctor/getFeedback/{woundImageID}")
    suspend fun getFeedbackList(@Path("woundImageID") woundImageID: String): Response<WoundImageFeedback>

    // Send feedback to specific wound image
    @POST("/doctor/sendFeedback")
    suspend fun sendFeedback(@Body params: Map<String, String>):
            Response<SendWoundFeedbackResponse>

    /** RESEARCHER ROUTES **/
    @GET("/researcher/getAllPatients")
    suspend fun getAllPatientsList(): Response<AssignedPatientsList>

    // Update user phone number
    @Multipart
    @PUT("/user/update_phone_number")
    suspend fun updatePhoneNumber(
        @Part("userContact1") userContact1: RequestBody,
        @Part("userContact2") userContact2: RequestBody,
        @Part("userID") userID: RequestBody,
    ): Response<UpdateDetailResponse>

    // To receive list of general ideas about surgery
    @GET("/utils/general")
    suspend fun getGeneralInfoList(): Response<GeneralInfoResponse>

    // Generate PDF and share to others

    @GET("/utils/getPdf/{entryID}")
    suspend fun getWoundImagePDF(@Path("entryID") entryID: String): Response<NetworkPDFGenerateResponse>

}