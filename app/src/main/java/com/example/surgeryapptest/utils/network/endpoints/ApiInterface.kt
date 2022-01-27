package com.example.surgeryapptest.utils.network.endpoints

import com.example.surgeryapptest.model.network.appointmentResponse.AppointmentNetworkResponse
import com.example.surgeryapptest.model.network.doctorResponse.getAssignedPatientList.AssignedPatientsList
import com.example.surgeryapptest.model.network.doctorResponse.getFeedbackResponse.WoundImageFeedback
import com.example.surgeryapptest.model.network.doctorResponse.sendFeedbackResponse.SendWoundFeedbackResponse
import com.example.surgeryapptest.model.network.passwordResetResponse.PasswordResetResponse
import com.example.surgeryapptest.model.network.patientResponse.deleteEntryNetworkResponse.NetworkDeleteEntryResponse
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntry
import com.example.surgeryapptest.model.network.patientResponse.getAllProgressBook.AllProgressBookEntryItem
import com.example.surgeryapptest.model.network.patientResponse.updateWoundImageResponse.NetworkUpdateEntryResponse
import com.example.surgeryapptest.model.network.patientResponse.uploadNewImageResponse.NetworkUploadNewEntryResponse
import com.example.surgeryapptest.model.network.pdfGenerationResponse.NetworkPDFGenerateResponse
import com.example.surgeryapptest.model.network.sendDetailsForOTP.SendOTPResponse
import com.example.surgeryapptest.model.network.updateDetails.UpdateDetailResponse
import com.example.surgeryapptest.model.network.userNetworkResponse.UserLoginNetworkResponse
import com.example.surgeryapptest.model.network.utilsResponse.GeneralInfoResponse
import com.example.surgeryapptest.model.network.verifyOTP.VerifiedOTPResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    companion object {
        const val BASE_URL = "/testingnodeapp" // empty or /testingnodeapp
    }

    // Authenticate the user
    @POST("${BASE_URL}/user/auth")
    suspend fun loginUser(@Body params: Map<String, String>):
            Response<UserLoginNetworkResponse>

    /** PATIENTS ROUTES */

    // To receive all the progress entry data
    // Edited: Get progress book by userID
    @GET("${BASE_URL}/books/progress/getAll/{userId}")
    suspend fun getAllProgressEntry(@Path("userId") userId: String): Response<AllProgressBookEntry>

    // To receive all the archived entries list
    // Edited: Get progress book by userID
    @GET("${BASE_URL}/books/progress/getAllArchived/{userId}")
    suspend fun getAllArchivedEntry(@Path("userId") userId: String): Response<AllProgressBookEntry>

    // To receive one progress entry by ID
    @GET("${BASE_URL}/books/progress/{entryid}")
    suspend fun getOneProgressEntry(): Response<AllProgressBookEntryItem>

    // To upload new wound image
    @Multipart
    @POST("${BASE_URL}/books/progress/upload")
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
    @PUT("${BASE_URL}/books/progress/edit")
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
    @PUT("${BASE_URL}/books/progress/deleteEntryNoFeedback")
    suspend fun deleteUploadedEntry(
        @Part("entryID") entryID: RequestBody
    ): Response<NetworkDeleteEntryResponse>

    // Archive selected image entry
    @Multipart
    @PUT("${BASE_URL}/books/progress/archive")
    suspend fun archiveUploadedEntry(
        @Part("entryID") entryID: RequestBody,
        @Part("prevFlag") prevFlag: RequestBody,
    ): Response<NetworkDeleteEntryResponse>

    /** DOCTOR ROUTES */

    // Retrieve assigned patients progress books
    @GET("${BASE_URL}/doctor/getAllPatients/{doctorId}")
    suspend fun getAssignedPatientsList(@Path("doctorId") doctorId: String): Response<AssignedPatientsList>

    // Retrieve specific wound image feedback
    @GET("${BASE_URL}/doctor/getFeedback/{woundImageID}")
    suspend fun getFeedbackList(@Path("woundImageID") woundImageID: String): Response<WoundImageFeedback>

    // Send feedback to specific wound image
    @POST("${BASE_URL}/doctor/sendFeedback")
    suspend fun sendFeedback(@Body params: Map<String, String>):
            Response<SendWoundFeedbackResponse>

    /** RESEARCHER ROUTES **/
    @GET("${BASE_URL}/researcher/getAllPatients")
    suspend fun getAllPatientsList(): Response<AssignedPatientsList>

    // Update user phone number
    @Multipart
    @PUT("${BASE_URL}/user/update_phone_number")
    suspend fun updatePhoneNumber(
        @Part("userContact1") userContact1: RequestBody,
        @Part("userContact2") userContact2: RequestBody,
        @Part("userID") userID: RequestBody,
    ): Response<UpdateDetailResponse>

    // To receive list of general ideas about surgery
    @GET("${BASE_URL}/utils/general")
    suspend fun getGeneralInfoList(): Response<GeneralInfoResponse>

    // Generate PDF and share to others

    @GET("${BASE_URL}/utils/getPdf/{entryID}")
    suspend fun getWoundImagePDF(@Path("entryID") entryID: String): Response<NetworkPDFGenerateResponse>

    /**
     * 1. Enter Verification Details
     * 2. Enter Received OTP
     * 3. Enter New Password
     */

    @POST("${BASE_URL}/user/sending_twilio_otp")
    suspend fun sendRegistrationIdPhoneNumber(@Body params: Map<String, String>): Response<SendOTPResponse>

    @POST("${BASE_URL}/user/verify_twilio")
    suspend fun sendOTPWithPhoneNumber(@Body params: Map<String, String>): Response<VerifiedOTPResponse>

    @POST("${BASE_URL}/user/reset_password")
    suspend fun resetPassword(@Body params: Map<String, String>): Response<PasswordResetResponse>

    /**
     * Appointment Routes
     */
    @GET("${BASE_URL}/utils/getAppointment/{userType}/{userID}")
    suspend fun getAppointmentList(@Path("userType") userType: String,
                                          @Path("userID") userID: String,
                                          ): Response<AppointmentNetworkResponse>

}