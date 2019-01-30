package com.avnet.emasia.webquote.extend.tag;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.convert.Converter;
import javax.faces.convert.DateTimeConverter;
import javax.faces.view.facelets.ConverterConfig;
import javax.faces.view.facelets.ConverterHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.TagAttribute;

public final class BreakListConverterHandle extends ConverterHandler {

    private final TagAttribute targetField;

    public BreakListConverterHandle(ConverterConfig config) {
        super(config);
        this.targetField = this.getAttribute("targetField");
        
    }

    /**
     * Returns a new DateTimeConverter
     * 
     * @see DateTimeConverter
     * @see com.sun.faces.facelets.tag.jsf.ConverterHandler#createConverter(com.sun.faces.facelets.FaceletContext)
     */
    protected Converter createConverter(FaceletContext ctx)
            throws FacesException, ELException, FaceletException {
        return ctx.getFacesContext().getApplication().createConverter(BreakListConverter.CONVERTER_ID);
    }

    /**
     * Implements tag spec, see taglib documentation.
     * 
     * @see com.sun.faces.facelets.tag.MetaTagHandler#setAttributes(com.sun.faces.facelets.FaceletContext, Object)
     */
    @Override
    public void setAttributes(FaceletContext ctx, Object obj) {
        BreakListConverter c = (BreakListConverter) obj;
        
        if (this.targetField != null) {
            c.setTargetField(this.targetField.getValue(ctx));
        }  
    }

    @Override
    public MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignoreAll();
    }
}
