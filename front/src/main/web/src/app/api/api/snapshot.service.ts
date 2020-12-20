/**
 * i18n Tool
 * Web API of the i18n tool
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *//* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent }                           from '@angular/common/http';
import { CustomHttpUrlEncodingCodec }                        from '../encoder';

import { Observable }                                        from 'rxjs';

import { ErrorMessagesDto } from '../model/errorMessagesDto';
import { SnapshotCreationDto } from '../model/snapshotCreationDto';
import { SnapshotDto } from '../model/snapshotDto';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class SnapshotService {

    protected basePath = 'http://127.0.0.1:8080';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (basePath) {
            this.basePath = basePath;
        }
        if (configuration) {
            this.configuration = configuration;
            this.basePath = basePath || configuration.basePath || this.basePath;
        }
    }

    /**
     * @param consumes string[] mime-types
     * @return true: consumes contains 'multipart/form-data', false: otherwise
     */
    private canConsumeForm(consumes: string[]): boolean {
        const form = 'multipart/form-data';
        for (const consume of consumes) {
            if (form === consume) {
                return true;
            }
        }
        return false;
    }


    /**
     * Creates a new snapshot based on the current tool state
     * 
     * @param body 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public create1(body: SnapshotCreationDto, observe?: 'body', reportProgress?: boolean): Observable<SnapshotDto>;
    public create1(body: SnapshotCreationDto, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<SnapshotDto>>;
    public create1(body: SnapshotCreationDto, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<SnapshotDto>>;
    public create1(body: SnapshotCreationDto, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (body === null || body === undefined) {
            throw new Error('Required parameter body was null or undefined when calling create1.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        return this.httpClient.request<SnapshotDto>('post',`${this.basePath}/api/snapshot`,
            {
                body: body,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Removes the specified snapshot
     * 
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public delete2(id: string, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public delete2(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public delete2(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public delete2(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling delete2.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<any>('delete',`${this.basePath}/api/snapshot/${encodeURIComponent(String(id))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Exports the content of the specified snapshot
     * 
     * @param id 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public exportZip(id: string, observe?: 'body', reportProgress?: boolean): Observable<Blob>;
    public exportZip(id: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Blob>>;
    public exportZip(id: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Blob>>;
    public exportZip(id: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling exportZip.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*',
            'application/zip'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Blob>('get',`${this.basePath}/api/snapshot/${encodeURIComponent(String(id))}/file`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Lists all available snapshots
     * 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public findAll2(observe?: 'body', reportProgress?: boolean): Observable<Array<SnapshotDto>>;
    public findAll2(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<SnapshotDto>>>;
    public findAll2(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<SnapshotDto>>>;
    public findAll2(observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<Array<SnapshotDto>>('get',`${this.basePath}/api/snapshot`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Import the specified snapshot ZIP file (the form must be composed of a file and optionally of the ZIP&#x27;s password)
     * 
     * @param encryptionPassword 
     * @param file 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public importZipForm(encryptionPassword?: string, file?: Blob, observe?: 'body', reportProgress?: boolean): Observable<SnapshotDto>;
    public importZipForm(encryptionPassword?: string, file?: Blob, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<SnapshotDto>>;
    public importZipForm(encryptionPassword?: string, file?: Blob, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<SnapshotDto>>;
    public importZipForm(encryptionPassword?: string, file?: Blob, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {



        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*',
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'multipart/form-data'
        ];

        const canConsumeForm = this.canConsumeForm(consumes);

        let formParams: { append(param: string, value: any): void; };
        let useForm = false;
        let convertFormParamsToString = false;
        // use FormData to transmit files using content-type "multipart/form-data"
        // see https://stackoverflow.com/questions/4007969/application-x-www-form-urlencoded-or-multipart-form-data
        useForm = canConsumeForm;
        if (useForm) {
            formParams = new FormData();
        } else {
            formParams = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        }

        if (encryptionPassword !== undefined) {
            formParams = formParams.append('encryptionPassword', <any>encryptionPassword) as any || formParams;
        }
        if (file !== undefined) {
            formParams = formParams.append('file', <any>file) as any || formParams;
        }

        return this.httpClient.request<SnapshotDto>('post',`${this.basePath}/api/snapshot/file`,
            {
                body: convertFormParamsToString ? formParams.toString() : formParams,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Restores the specified snapshot, the tool state will be restored to that state.
     * 
     * @param id 
     * @param action 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public restore(id: string, action?: string, observe?: 'body', reportProgress?: boolean): Observable<SnapshotDto>;
    public restore(id: string, action?: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<SnapshotDto>>;
    public restore(id: string, action?: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<SnapshotDto>>;
    public restore(id: string, action?: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (id === null || id === undefined) {
            throw new Error('Required parameter id was null or undefined when calling restore.');
        }


        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (action !== undefined && action !== null) {
            queryParameters = queryParameters.set('action', <any>action);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            '*/*'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.request<SnapshotDto>('post',`${this.basePath}/api/snapshot/${encodeURIComponent(String(id))}/do`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
