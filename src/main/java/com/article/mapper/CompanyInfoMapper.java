package com.article.mapper;

import com.article.entity.CompanyInfo;
import com.article.entity.Identified;
import com.article.entity.TmpExport;
import org.json.JSONObject;

public interface CompanyInfoMapper {

    default JSONObject companyInfoToJson (Identified identified) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id_seq", identified.getIdSeq());
        jsonObject.put("companyName", identified.getCompanyName());
        jsonObject.put("ceoName", identified.getCeoName());
        return jsonObject;

    }

    default JSONObject companyInfoToJsonExport (TmpExport tmpExport) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id_seq", tmpExport.getIdSeq());
        jsonObject.put("companyName", tmpExport.getCompanyName());
        jsonObject.put("ceoName", tmpExport.getCeoName());
        return jsonObject;

    }

}
