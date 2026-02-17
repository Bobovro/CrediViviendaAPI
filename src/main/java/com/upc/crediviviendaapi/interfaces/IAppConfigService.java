package com.upc.crediviviendaapi.interfaces;

import com.upc.crediviviendaapi.dtos.AppConfigDTO;

public interface IAppConfigService {
    AppConfigDTO getConfig();
    AppConfigDTO updateConfig(AppConfigDTO dto);
}
