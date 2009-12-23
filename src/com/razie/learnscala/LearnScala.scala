package com.razie.learnscala

object LearnScala extends Application {
   
   println (typecasting) 

   folding

   doregexp("@john[@gigi==4]")
   doregexp("john[@gigi==4]")
   doregexp("@john")
   doregexp("john")

   var traceIsOn = false
   var i = 0
   def trace ( f: => Any) = if (traceIsOn) println (f)
   def inc () = { i = i+ 1; i }
  
   
   trace ( "trace: " + inc )
   trace ( "trace: " + inc )
   trace ( "trace: " + inc )
   traceIsOn=true
   trace ( "trace: " + inc )
   
   val cc : AScalaExtension = (new AScalaExtension)
   cc.takesOptionalArgs ("itsi", "bitsi", "spider")

   // this particular one doesn't quite work
   implicit def itos (x:java.lang.Integer):String = String.valueOf(x)

   def typecasting () = {
      3.asInstanceOf[Integer]
   }

   def folding = {
      println ("foldleft: "+List("1","2","3").foldLeft("foldleft")((x,y)=>x+"("+y+")"))    
      println ("foldright: "+List("1","2","3").foldRight("foldright")((x,y)=>x+"("+y+")"))    
      println ("reduceleft: "+List("1","2","3").reduceLeft((x,y)=>x+"("+y+")"))    
      println ("reduceright: "+List("1","2","3").reduceRight((x,y)=>x+"("+y+")"))    
   } 

   def doregexp (expr:String) = {
      val parse = """([@])*(\w+)(\[.*\])*""".r
      val parse( attr, name, scond) = expr

      println("EXPR:"+expr+"  parsed:"+attr+":"+name+":"+scond)
   }

   // TODO revive this when scala 2.8 is final
//   implicit def jltoa[A](ij:java.util.List[A]) : Array[A] = {
//      val l:Array[A] = new Array[A](ij.size)
//
//      //TODO optimize this or even remove it
//      for (i <- 0 to ij.size-1)
//         l(i) = ij.get(i)
//
//         l
//   }
//
//   //TODO optimize this or even remove it
//   implicit def jltol[A](ij:java.util.List[A]) : List[A] = {
//      val l:Array[A] = ij
//
//      l.toList
//   }

}

// TODO this is broken in scala 2.8

class AScalaExtension extends ABaseJavaClass {
//   override def takesOptionalArgs (x:String*) = {
//      println ("Scala println: ", x.mkString(" "))
//      java.lang.System.out.printf ("Java printf: %s %s %s", x:_*)
//   }
   
   def justScalaOpt (x:String*) = {
      println ("More scala printing: ")
      x.foreach (println)
      takesOptionalArgs (x:_*)
   }
}


//--------------- variance problem

trait Serializer [-T] {
  def serialize[U<:T](x:U):U 
}

// can serialize strings...
class StrSer extends Serializer[String] {
   override def serialize[U<:String](x:U):U = x
}

class Blobber[T] {
   def blob (x:Serializer[T],l:List[T]) = 
     l.foldLeft ("") ((a,b) => x.serialize(b) + " ")
//     for (e <- l) yield x.serialize(e) + " "
}

object MyMain extends Application {
   println ((new Blobber[String]) blob (new StrSer, List("itsi","bitsi","spider")))
}

object MoreStuff {
// implicit def tomap[A,B] (m:java.util.Map[A,B]) : scala.collection.mutable.Map[A,B] = {
//    val ret = new scala.collection.mutable.HashMap[A,B]
//    val iter = m.entrySet.iterator
//    while (iter.hasNext) {
//       val e = iter.next
//       ret.put (e.getKey, e.getValue)
//    }
//    ret
// }

// implicit def toarray[A](ij:java.util.List[A]) : Array[A] = {
//    scala.collection.JavaConversions.asBuffer(ij).toArray
//    val l:Array[A] = new Array[A](ij.size)
//
//    //TODO optimize this or even remove it
//    for (i <- 0 to ij.size-1)
//       l(i) = ij.get(i)
//  
//    l
// }

// implicit def xtolist[A](ij:java.util.List[A]) : List[A] = {
//    val l:Array[A] = ij
//    l.toList
// }

   implicit def toRazList[T](enumeration:java.util.List[T]):RazList[T] =
      new RazList(enumeration)
  
   implicit def toRazList1[T](enumeration:java.lang.Iterable[T]):RazList1[T] =
      new RazList1(enumeration)
}

class RazList[T](enumeration:java.util.List[T]) extends Iterator[T] {
   val iter = enumeration.iterator
   def hasNext:Boolean =  iter.hasNext
   def next:T = iter.next
}

class RazList1[T](enumeration:java.lang.Iterable[T]) extends Iterator[T] {
   val iter = enumeration.iterator
   def hasNext:Boolean =  iter.hasNext
   def next:T = iter.next
}

