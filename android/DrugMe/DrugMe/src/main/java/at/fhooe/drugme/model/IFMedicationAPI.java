package at.fhooe.drugme.model;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by ch on 03.12.13.
 */
public interface IFMedicationAPI {
    @FormUrlEncoded
    @POST("/patients")
    Object registerUser(@Field("ec") String ec, @Field("name") String name,@Field("apikey") String apikey);
}
