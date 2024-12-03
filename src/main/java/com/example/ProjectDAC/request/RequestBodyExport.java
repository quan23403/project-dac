package com.example.ProjectDAC.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestBodyExport {
    private List<Long> ids;
}
