package com.oa.cgpg.dataOperations;

import com.oa.cgpg.models.opinionNetEntity;

import java.util.List;

/**
 * Created by Izabela on 2014-11-09.
 */
public interface AsyncResponse {
    public void processFinish(String output);

    public void processFinishOpinion(List<opinionNetEntity> list);
}
