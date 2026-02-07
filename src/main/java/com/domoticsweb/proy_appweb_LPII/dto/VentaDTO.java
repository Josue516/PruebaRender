package com.domoticsweb.proy_appweb_LPII.dto;

import lombok.Data;
import java.util.List;

@Data
public class VentaDTO {
    private List<CarritoDTO> items;
    private String orderId;
}