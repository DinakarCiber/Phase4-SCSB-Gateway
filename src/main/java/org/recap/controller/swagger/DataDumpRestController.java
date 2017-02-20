package org.recap.controller.swagger;

import io.swagger.annotations.*;
import org.recap.ReCAPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by premkb on 19/8/16.
 */

@RestController
@RequestMapping("/dataDump")
@Api(value="dataDump", description="Export data dump", position = 1)
public class DataDumpRestController {

    private static final Logger logger = LoggerFactory.getLogger(DataDumpRestController.class);

    @Value("${server.protocol}")
    private String serverProtocol;

    @Value("${scsb.etl.url}")
    private String scsbEtlUrl;

    @RequestMapping(value="/exportDataDump", method = RequestMethod.GET)
    @ApiOperation(value = "exportDataDump",
            notes = "Export datadumps to institutions", nickname = "exportDataDump", position = 0)
    @ApiResponses(value = {@ApiResponse(code = 200, message = ReCAPConstants.DATADUMP_PROCESS_STARTED)})
    @ResponseBody
    public ResponseEntity exportDataDump(@ApiParam(value = "Code of institutions whose shared collection updates are requested. Use PUL for Princeton, CUL for Columbia and NYPL for NYPL." , required = true, name = "institutionCodes") @RequestParam String institutionCodes,
                                         @ApiParam(value = "Code of insitituion who is requesting. Use PUL for Princeton, CUL for Columbia and NYPL for NYPL. ",required=true, name = "requestingInstitutionCode") @RequestParam String requestingInstitutionCode,
                                         @ApiParam(value = "Type of export - Incremental (use 1) or Deleted (use 2)" , required = true , name = "fetchType") @RequestParam String fetchType,
                                         @ApiParam(value = "Type of format - Marc xml (use 0) or SCSB xml (use 1), for deleted records only json format (use 2)",required=true, name = "outputFormat") @RequestParam String outputFormat,
                                         @ApiParam(value = "Get updates to middleware collection since the date provided. Default will be updates since the previous day. Date format will be a string (yyyy-MM-dd HH:mm)", name = "date") @RequestParam(required=false) String date,
                                         @ApiParam(value = "Collection group id will get the relevant info based on the id provided. Default will get both shared and open information - Shared (use 1), Open (use 2), Both (use 1,2)", name = "collectionGroupIds") @RequestParam(required=false) String collectionGroupIds,
                                         @ApiParam(value = "Type of transmission - FTP (use 0), HTTP Response (use 1) this parameter is not considered for full dump. Default will be ftp ", name = "transmissionType")@RequestParam(required=false) String transmissionType,
                                         @ApiParam(value = "Email address to whom we need to send an email" , name = "emailToAddress")@RequestParam(required=false) String emailToAddress
    ){
        RestTemplate restTemplate = new RestTemplate();
        Map<String,String> inputMap = new HashMap<>();
        inputMap.put("institutionCodes",institutionCodes);
        inputMap.put("requestingInstitutionCode",requestingInstitutionCode);
        inputMap.put("fetchType",fetchType);
        inputMap.put("outputFormat",outputFormat);
        inputMap.put("date",date);
        inputMap.put("collectionGroupIds",collectionGroupIds);
        inputMap.put("transmissionType",transmissionType);
        inputMap.put("emailToAddress",emailToAddress);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api_key","recap");
            HttpEntity requestEntity = new HttpEntity(headers);
            ResponseEntity<String> response = restTemplate.exchange(serverProtocol + scsbEtlUrl + "dataDump/exportDataDump/?institutionCodes={institutionCodes}&requestingInstitutionCode={requestingInstitutionCode}&fetchType={fetchType}&outputFormat={outputFormat}&date={date}&collectionGroupIds={collectionGroupIds}&transmissionType={transmissionType}&emailToAddress={emailToAddress}", HttpMethod.GET, requestEntity, String.class, inputMap);
            ResponseEntity responseEntity = new ResponseEntity(response.getBody(), getHttpHeaders(), getHttpStatus(response.getBody()));
            return responseEntity;
        } catch (Exception exception) {
            exception.printStackTrace();
            ResponseEntity responseEntity = new ResponseEntity("Scsb Etl Service is Unavailable.", getHttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE);
            return responseEntity;
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(ReCAPConstants.RESPONSE_DATE, new Date().toString());
        return responseHeaders;
    }

    private HttpStatus getHttpStatus(String message){
        if(message.equals(ReCAPConstants.DATADUMP_PROCESS_STARTED) || message.equals(ReCAPConstants.DATADUMP_NO_RECORD)){
            return HttpStatus.OK;
        }else{
            return HttpStatus.BAD_REQUEST;
        }
    }
}
