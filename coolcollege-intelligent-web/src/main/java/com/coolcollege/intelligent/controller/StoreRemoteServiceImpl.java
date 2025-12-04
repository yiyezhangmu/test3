package com.coolcollege.intelligent.controller;

import org.springframework.stereotype.Service;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.facade.dto.ResultDTO;
import com.coolcollege.intelligent.facade.remote.StoreRemoteService;

@Service
@SofaService(uniqueId = "com.coolcollege.intelligent.service.storeRemoteService", interfaceType = StoreRemoteService.class,
    bindings = {@SofaServiceBinding(bindingType = "bolt")})
public class StoreRemoteServiceImpl implements StoreRemoteService {

    @Override
    public ResultDTO<Integer> getTest(int aa) {
        ResultDTO<Integer> r = new ResultDTO<Integer>(aa);
        System.out.println("test remote service ..............." + aa);
        return r;
    }

}
