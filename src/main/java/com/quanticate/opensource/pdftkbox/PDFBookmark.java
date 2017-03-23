/* ====================================================================
  Copyright 2017 Quanticate Ltd

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
==================================================================== */
package com.quanticate.opensource.pdftkbox;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;

/**
 * Wrapper around PDFBox bookmarks to provide (just) the
 *  information we need
 * 
 * TODO Helper class for PDFBox decoding
 */
public class PDFBookmark {
   public enum ZoomType {
      Inherit, FitPage, FitWidth, FitHeight, ZoomPercent
   };
   
   private PDOutlineItem outlineItem;
   private String title;
   private int level;
   private int pageNumber;
   private int yOffset;
   private ZoomType zoomType;
   private String zoom;
   
   /**
    * Creates our Bookmark Wrapper from the outline item.
    * Handling Children (and tracking of levels) is up to
    *  the calling class to manage
    */
   public PDFBookmark(PDOutlineItem current, int level) throws IOException {
      this.outlineItem = current;
      this.level = level;
      
      // Set defaults
      this.pageNumber = -1;
      this.yOffset = 0;
      
      // Find where the bookmark points to and record
      PDDestination dest = null;

      // Check for a bookmark via an action
      if (current.getAction() != null) {
         PDAction action = current.getAction();
         if (action instanceof PDActionGoTo) {
            dest = ((PDActionGoTo)action).getDestination();
         }
      }
      if (dest == null) {
         dest = current.getDestination();
      }

      if (dest != null) {
         this.title = current.getTitle();

         if (dest instanceof PDPageDestination) {
            PDPageDestination pdest = (PDPageDestination)dest;
            int pageNum = pdest.retrievePageNumber();
            if (pageNum != -1) {
               this.pageNumber = pageNum+1;
            }
         }

         // TODO
         if (dest instanceof PDPageXYZDestination) {
            PDPageXYZDestination xyz = (PDPageXYZDestination)dest;
            yOffset = xyz.getTop();
            
            if (xyz.getZoom() > 0) {
               zoomType = ZoomType.ZoomPercent;
               zoom = Integer.toString((int)(xyz.getZoom()*100));
            } else {
               zoomType = ZoomType.Inherit;
               zoom = zoomType.name();
            }
         }
         else if (dest instanceof PDPageFitDestination) {
            // etc
         }

         System.err.println("TODO: " + dest);
      }
   }

   protected PDOutlineItem getOutlineItem()
   {
      return outlineItem;
   }

   public String getTitle()
   {
      return title;
   }

   /**
    * Get the Bookmark (indent) level, from 1+
    */
   public int getLevel()
   {
      return level;
   }

   /**
    * Get the number of the Page this bookmark refers
    *  to (1+), or -1 if this isn't a page-based bookmark
    */
   public int getPageNumber()
   {
      return pageNumber;
   }

   public int getYOffset()
   {
      return yOffset;
   }

   public ZoomType getZoomType()
   {
      return zoomType;
   }

   public String getZoom()
   {
      return zoom;
   }
}
