package org.alfresco.web.ui.common.component;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.springframework.extensions.webscripts.ui.common.component.SelfRenderingComponent;

/**
 * Allows a group of UIListItem objects to be represented.
 * 
 * @author gavinc
 */
public class UIListItems extends SelfRenderingComponent
{
   private Object value;
   private boolean cacheValue;
   
   /**
    * @see javax.faces.component.UIComponent#getFamily()
    */
   public String getFamily()
   {
      return "org.alfresco.faces.ListItems";
   }
   
   /**
    * @return Returns the object holding the decriptions
    */
   public Object getValue()
   {
      if (getCacheValue() == false || this.value == null)
      {
         ValueBinding vb = getValueBinding("value");
         if (vb != null)
         {
            this.value = vb.getValue(getFacesContext());
         }
      }
      return this.value;
   }

   /**
    * @param value Sets the object holding the description
    */
   public void setValue(Object value)
   {
      this.value = value;
   }

   /**
    * @return the cacheValue
    */
   public boolean getCacheValue()
   {
      ValueBinding vb = getValueBinding("cacheValue");
      if (vb != null)
      {
         this.cacheValue = (Boolean)vb.getValue(getFacesContext());;
      }
      return this.cacheValue;
   }

   /**
    * @param cacheValue the cacheValue to set
    */
   public void setCacheValue(boolean cacheValue)
   {
      this.cacheValue = cacheValue;
   }

   /**
    * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
    */
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[])state;
      // standard component attributes are restored by the super class
      super.restoreState(context, values[0]);
      this.value = values[1];
      this.cacheValue = (Boolean)values[2];
   }
   
   /**
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
   public Object saveState(FacesContext context)
   {
      // standard component attributes are saved by the super class
      return new Object[] {
            super.saveState(context),
            this.value,
            this.cacheValue
         };
   }
}

