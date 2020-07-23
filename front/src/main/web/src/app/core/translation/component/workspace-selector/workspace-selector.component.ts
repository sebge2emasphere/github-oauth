import {
    AfterViewInit,
    Component,
    DoCheck,
    ElementRef,
    forwardRef,
    HostBinding,
    Injector,
    Input,
    OnDestroy,
    OnInit,
    ViewChild
} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, NgControl} from "@angular/forms";
import {MatFormFieldControl} from "@angular/material/form-field";
import {Workspace} from "../../../../translations/model/workspace.model";
import {MatAutocomplete, MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {combineLatest, Observable, Subject} from "rxjs";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {FocusMonitor} from "@angular/cdk/a11y";
import {map, startWith, takeUntil} from "rxjs/operators";
import {coerceBooleanProperty} from "@angular/cdk/coercion";
import * as _ from "lodash";
import {MatChipInputEvent} from "@angular/material/chips";
import {WorkspaceService} from "../../../../translations/service/workspace.service";

@Component({
    selector: 'app-workspace-selector',
    templateUrl: './workspace-selector.component.html',
    styleUrls: ['./workspace-selector.component.css'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => WorkspaceSelectorComponent),
            multi: true
        },
        {provide: MatFormFieldControl, useExisting: WorkspaceSelectorComponent}
    ],
})
// https://itnext.io/creating-a-custom-form-field-control-compatible-with-reactive-forms-and-angular-material-cf195905b451
// https://material.angular.io/guide/creating-a-custom-form-field-control
export class WorkspaceSelectorComponent implements OnInit, OnDestroy, AfterViewInit, DoCheck, ControlValueAccessor, MatFormFieldControl<Workspace[]> {
    @Input() public labelKey: string = 'SHARED.WORKSPACES_LABEL';

    @ViewChild('auto', {static: false}) public matAutocomplete: MatAutocomplete;
    @HostBinding('attr.aria-describedby') public describedBy = '';
    @HostBinding() public id = `app-workspace-selector-${WorkspaceSelectorComponent.nextId++}`;

    public stateChanges = new Subject<void>();
    public focused = false;
    public controlType = 'app-workspace-selector';
    public errorState = false;
    public ngControl: NgControl;

    public readonly parts: FormGroup;
    public filteredWorkspaces$: Observable<Workspace[]>;
    public readonly separatorKeysCodes: number[] = [ENTER, COMMA];

    private static nextId = 0;

    private _remainingAvailableWorkspaces$: Observable<Workspace[]>;

    private onChange = (_: any) => {
    };
    private onTouched = () => {
    };
    private _placeholder: string;
    private _disabled = false;
    private _required = false;
    private readonly _destroyed$ = new Subject();

    constructor(private injector: Injector,
                private focusMonitor: FocusMonitor,
                private formBuilder: FormBuilder,
                private elRef: ElementRef<HTMLElement>,
                private _workspaceService: WorkspaceService) {
        this.parts = formBuilder.group({
            input: [null],
            list: [[]]
        });
    }

    ngOnInit() {
        this.ngControl = this.injector.get(NgControl);

        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }

        this._remainingAvailableWorkspaces$ = combineLatest([this._workspaceService.getWorkspaces(), this.parts.valueChanges.pipe(startWith(<string>null))])
            .pipe(
                takeUntil(this._destroyed$),
                map(([availableWorkspaces, _]) => this.filterOutPresent(availableWorkspaces, this.value))
            );

        this.filteredWorkspaces$ = combineLatest([this.parts.controls['input'].valueChanges.pipe(startWith(<string>null)), this._remainingAvailableWorkspaces$])
            .pipe(
                takeUntil(this._destroyed$),
                map(([workspace, remainingAvailableWorkspaces]) => workspace ? this.filterRemaining(workspace, remainingAvailableWorkspaces) : remainingAvailableWorkspaces)
            );
    }

    ngAfterViewInit(): void {
        this.focusMonitor
            .monitor(this.elRef.nativeElement, true)
            .subscribe(origin => {
                this.focused = !!origin;
                this.stateChanges.next();
            });
    }

    ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();

        this.focusMonitor.stopMonitoring(this.elRef.nativeElement);
        this.stateChanges.complete();
    }

    ngDoCheck(): void {
        if (this.ngControl) {
            this.errorState = this.ngControl.invalid && this.ngControl.touched;
            this.stateChanges.next();
        }
    }

    @Input()
    get placeholder(): string {
        return this._placeholder;
    }

    set placeholder(value: string) {
        this._placeholder = value;
        this.stateChanges.next();
    }

    @Input()
    get disabled(): boolean {
        return this._disabled;
    }

    set disabled(value: boolean) {
        this._disabled = coerceBooleanProperty(value);
        this._disabled ? this.parts.disable() : this.parts.enable();
        this.stateChanges.next();
    }

    @Input()
    get required() {
        return this._required;
    }

    set required(req) {
        this._required = coerceBooleanProperty(req);
        this.stateChanges.next();
    }

    get value(): Workspace[] {
        return this.parts.controls['list'].value;
    }

    set value(value: Workspace[]) {
        this.writeValue(value);

        this.onChange(value);
        this.stateChanges.next();
    }

    get empty() {
        return _.isEmpty(this.parts.controls['list']);
    }

    @HostBinding('class.floating')
    get shouldLabelFloat() {
        return this.focused || !this.empty;
    }

    setDescribedByIds(ids: string[]) {
        this.describedBy = ids.join(' ');
    }

    onContainerClick(event: MouseEvent) {
        if ((event.target as Element).tagName.toLowerCase() != 'input') {
            this.elRef.nativeElement.querySelector('input').focus();
        }
    }

    writeValue(values: Workspace[] | null): void {
        this.parts.controls['list'].setValue(values);
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        this.add(<Workspace><unknown>event.option.value);
    }

    add(workspace: Workspace) {
        this.parts.controls['input'].setValue(null);

        const copy = _.clone(this.value);
        copy.push(workspace);

        this.setValue(copy);
    }

    remove(workspace: Workspace): void {
        const index = this.getIndex(workspace, this.value);

        if (index >= 0) {
            const copy = _.clone(this.value);
            copy.splice(index, 1);

            this.setValue(copy);
        }
    }

    onTokenEnd(event: MatChipInputEvent): void {
        if (!this.matAutocomplete.isOpen) {
            const input = event.input;

            if (input) {
                input.value = '';
            }
        }
    }

    private setValue(value: Workspace[]) {
        this.value = value;
        this.stateChanges.next();
    }

    private getIndex(workspace: Workspace, workspaces: Workspace[]) {
        return _.findIndex(workspaces, first => workspace.equals(first));
    }

    private filterOutPresent(workspacesToFilter: Workspace[], others: Workspace[]): Workspace[] {
        return workspacesToFilter.filter(workspace => this.getIndex(workspace, others) < 0);
    }

    private filterRemaining(value: string | Workspace, remainingAvailableWorkspaces: Workspace[]): Workspace[] {
        if (!value) {
            return remainingAvailableWorkspaces;
        } else if (value instanceof Workspace) {
            return remainingAvailableWorkspaces.filter(locale => !locale.equals(value));
        } else {
            const filterValue = value.toLowerCase();

            return remainingAvailableWorkspaces.filter(workspace => workspace.branch.toLowerCase().indexOf(filterValue) === 0);
        }
    }

}
