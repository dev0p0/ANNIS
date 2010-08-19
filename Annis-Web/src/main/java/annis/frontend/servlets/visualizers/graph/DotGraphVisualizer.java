/*
 *  Copyright 2010 Collaborative Research Centre SFB 632.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package annis.frontend.servlets.visualizers.graph;

import annis.frontend.servlets.MatchedNodeColors;
import annis.frontend.servlets.visualizers.WriterVisualizer;
import annis.model.AnnisNode;
import annis.model.Edge;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Krause
 */
public class DotGraphVisualizer extends WriterVisualizer
{

  private String outputFormat = "png";
  private int scale = 50;
  private StringBuilder nodeDef;
  private StringBuilder edgeDef;

  @Override
  public void writeOutput(Writer writer)
  {
    nodeDef = new StringBuilder();
    edgeDef = new StringBuilder();
    for (AnnisNode n : getResult().getGraph().getNodes())
    {
      writeNode(n);
    }
    for (Edge e : getResult().getGraph().getEdges())
    {
      writeEdge(e);
    }

    try
    {
      String cmd = getDotPath() + " -s" + scale + ".0 -T" + outputFormat;
      Runtime runTime = Runtime.getRuntime();
      Process p = runTime.exec(cmd);
      StringWriter debugStdin = new StringWriter();
      OutputStreamWriter stdin = new OutputStreamWriter(p.getOutputStream(), "UTF-8");
      writeDOT(stdin);
      writeDOT(debugStdin);

      stdin.flush();

      p.getOutputStream().close();
      int chr;
      InputStream stdout = p.getInputStream();
      StringBuilder outMessage = new StringBuilder();
      while ((chr = stdout.read()) != -1)
      {
        writer.write(chr);
        outMessage.append((char) chr);
      }

      StringBuilder errorMessage = new StringBuilder();
      InputStream stderr = p.getErrorStream();
      while ((chr = stderr.read()) != -1)
      {
        errorMessage.append((char) chr);
      }

      p.destroy();
      writer.flush();

      if(!"".equals(errorMessage.toString()))
      {
        Logger.getLogger(DotGraphVisualizer.class.getName()).log(
          Level.SEVERE,
          "Could not execute dot graph-layouter.\ncommand line:\n{0}\n\nstderr:\n{1}\n\nstdin:\n{2}",
          new Object[]{cmd, errorMessage.toString(), debugStdin.toString()});
      }

    }
    catch (IOException ex)
    {
      Logger.getLogger(DotGraphVisualizer.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  private void writeDOT(Writer writer) throws IOException
  {
    writer.append("digraph G {\n");

    writer.append(nodeDef);
    writer.append(edgeDef);

    writer.append("}");
  }

  private void writeNode(AnnisNode node)
  {
    nodeDef.append("\t");
    nodeDef.append(node.getId());
    // attributes
    nodeDef.append(" [ ");
    // output label
    nodeDef.append("label=\"");
    if(node.isToken())
    {
      nodeDef.append(node.getSpannedText());
    }
    else
    {
      nodeDef.append(node.getName());
    }
    nodeDef.append("\" ");
    // background color
    nodeDef.append("style=filled fillcolor=\"");
    String colorAsString = getMarkableExactMap().get(Long.toString(node.getId()));
    if(colorAsString != null)
    {
      MatchedNodeColors color = MatchedNodeColors.valueOf(colorAsString);
      nodeDef.append(color.getHTMLColor());
    }
    else
    {
      nodeDef.append("#ffffff");
    }
    nodeDef.append("\" ");
    // "footer"
    nodeDef.append("];\n");

    // TODO: node annotations, spanned text and/or name
  }

  private void writeEdge(Edge edge)
  {
    edgeDef.append("\t");
    edgeDef.append(edge.getSource() == null ? null : edge.getSource().getId());
    edgeDef.append(" -> ");
    edgeDef.append(edge.getDestination() == null ? null : edge.getDestination().getId());
    edgeDef.append(";\n");
    // TODO
  }

  @Override
  public String getContentType()
  {
    return "image/png";
  }

  @Override
  public String getCharacterEncoding()
  {
    return "ISO-8859-1";
  }


}
