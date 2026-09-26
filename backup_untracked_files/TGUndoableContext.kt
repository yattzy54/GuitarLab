package app.tuxguitar.android.action.listener.undoable
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.editor.undo.TGUndoableEdit
class TGUndoableContext { private val edits=HashMap<Int,TGUndoableEdit>();var level=0;var undoable:TGUndoableJoined?=null;fun reset(){level=0;undoable=null;edits.clear()};fun addUndoableToCurrentLevel(e:TGUndoableEdit){edits[level]=e};fun getUndoableFromCurrentLevel()=edits[level];fun incrementLevel(){level++};fun decrementLevel(){level--};companion object{fun getInstance(c:TGActionContext):TGUndoableContext{val k=TGUndoableContext::class.java.name;return if(c.hasAttribute(k))c.getAttribute(k) else TGUndoableContext().also{c.setAttribute(k,it)}}}}
