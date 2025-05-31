package com.til.light_iot_cloud.data.input;

import com.til.light_iot_cloud.data.Light;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DetectionInput {

    List<DetectionItemInput> items;
    MultipartFile image;

}
