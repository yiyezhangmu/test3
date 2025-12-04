package com.coolcollege.intelligent.rpc.fileUpload;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.coolcollege.intelligent.model.fileUpload.FileUploadParam;
import com.coolstore.base.dto.ResultDTO;
import com.coolstore.license.client.api.FileUploadApi;
import com.coolstore.license.client.constants.CoolStoreLicenseConstants;
import com.coolstore.license.client.dto.PrivateImageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * @author CFJ
 * @version 1.0
 * @date 2023/7/24 17:23
 */
@Slf4j
@Service
public class FileUploadApiService {
    @SofaReference(uniqueId = CoolStoreLicenseConstants.LICENSE_FILEUPLOAD_FACADE_UNIQUE_ID, interfaceType = FileUploadApi.class,
            binding = @SofaReferenceBinding(bindingType = CoolStoreLicenseConstants.SOFA_BINDING_DUBBO_TYPE))
    private FileUploadApi fileUploadApi;

    /**
     * 上传私有图片
     * @param enterpriseId,waterMark,multipartFile
     * @return
     */
    public FileUploadParam uploadPrivateImage(String enterpriseId,MultipartFile multipartFile, String waterMark) {
        if (Objects.isNull(waterMark)) {
            return null;
        }
        PrivateImageDTO privateImageDTO = new PrivateImageDTO();
        privateImageDTO.setFile(multipartFile);
        privateImageDTO.setEnterpriseId(enterpriseId);
        privateImageDTO.setWaterMark(waterMark);
        ResultDTO resultDTO = fileUploadApi.uploadPrivateImage(enterpriseId, multipartFile, waterMark);
        if (!resultDTO.isSuccess()) {
            return null;
        }
        FileUploadParam data = (FileUploadParam) resultDTO.getData();
        return data;
    }


}
