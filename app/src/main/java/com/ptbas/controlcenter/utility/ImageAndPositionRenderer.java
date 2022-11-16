package com.ptbas.controlcenter.utility;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

public class ImageAndPositionRenderer implements PdfPCellEvent {

    protected Image image;

    public ImageAndPositionRenderer(Image image) {
        this.image = image;
    }

    public void cellLayout(PdfPCell cell, Rectangle position,
                           PdfContentByte[] canvases) {
        try {
            PdfContentByte cb = canvases[PdfPTable.BACKGROUNDCANVAS];
            image.scaleAbsolute(position);
            image.setAbsolutePosition(position.getLeft(), position.getBottom());
            cb.addImage(image);
        } catch (DocumentException e) {
            throw new ExceptionConverter(e);
        }
    }
}