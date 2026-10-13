import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export interface ConfirmDialogData {
  title: string;
  message: string;
  confirmLabel: string;
}

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<h2 mat-dialog-title data-testid="confirm-title">{{ data.title }}</h2><mat-dialog-content><p data-testid="confirm-message">{{ data.message }}</p></mat-dialog-content><mat-dialog-actions align="end"><button mat-button [mat-dialog-close]="false" data-testid="cancel-action">Cancel</button><button mat-flat-button color="warn" [mat-dialog-close]="true" data-testid="confirm-action">{{ data.confirmLabel }}</button></mat-dialog-actions>`,
  styles: [`:host{display:block;min-width:min(420px,calc(100vw - 48px))}p{line-height:1.5;color:#465169}`]
})
export class ConfirmDialogComponent {
  readonly data = inject<ConfirmDialogData>(MAT_DIALOG_DATA);
}
