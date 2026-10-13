import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-file-upload-box',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `<label class="filebox" data-testid="file-upload-box"><span>{{ label }}</span><input type="file" [attr.accept]="accept" (change)="selected($event)" data-testid="import-file"></label>`,
  styles: [`.filebox{display:block;padding:20px;border:1px dashed #aab4c5;border-radius:10px;background:#fbfcfe;cursor:pointer;margin:14px 0;color:#39445c;font-weight:600}.filebox:hover{border-color:#5549bc;background:#f7f6ff}.filebox input{display:block;margin-top:12px;max-width:100%;font-weight:400}`]
})
export class FileUploadBoxComponent {
  @Input() label = 'Choose a file';
  @Input() accept = '';
  @Output() fileSelected = new EventEmitter<File | null>();

  selected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.fileSelected.emit(input.files?.item(0) ?? null);
  }
}
