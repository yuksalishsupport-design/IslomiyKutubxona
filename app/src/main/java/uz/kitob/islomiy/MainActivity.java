package uz.kitob.islomiy;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.io.*;

public class MainActivity extends Activity {
  final String[] names={"Ar-rahiq al-maxtum", "Nurul-yaqin", "Allohning go‘zal ismlari"};
  final String[] authors={"Safiyurrahmon Muborakpuriy", "Muhammad Xuzariy", "Muhammad Rotib Nablusiy asari asosida"};
  final String[] files={"Ar-rahiq al-maxtum (Muhrlangan jannat sharobi).pdf", "Muhammad Xuzariy. Nurul-yaqin.pdf", "Allohning go'zal ismlari.pdf"};
  final int[] pages={378,171,208};
  final int green=Color.rgb(12,77,63), gold=Color.rgb(198,155,78);
  LinearLayout root,bar; ImageView pageView; TextView counter; SeekBar seek;
  PdfRenderer renderer; ParcelFileDescriptor descriptor; int book=-1,page=0; float zoom=1f; boolean dark=false;
  android.content.SharedPreferences prefs;
  @Override public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("reading",0);home();}
  TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setPadding(16,12,16,12);return t;}
  LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
  void setup(){root=column();root.setBackgroundColor(Color.rgb(248,247,242));setContentView(root);}
  void header(String title){TextView t=text(title,23,Color.WHITE);t.setBackgroundColor(green);t.setGravity(Gravity.CENTER_VERTICAL);t.setPadding(24,28,12,28);root.addView(t);}
  Button button(String title,LinearLayout target,Runnable action){Button b=new Button(this);b.setText(title);b.setAllCaps(false);b.setOnClickListener(v->action.run());target.addView(b);return b;}
  void home(){closePdf();book=-1;setup();header("☪  Islomiy kutubxona");TextView intro=text("3 ta kitob  •  757 sahifa  •  Internetsiz mutolaa",15,green);root.addView(intro);
    ScrollView scroll=new ScrollView(this);root.addView(scroll);LinearLayout list=column();list.setPadding(14,8,14,8);scroll.addView(list);
    for(int i=0;i<3;i++){final int id=i;LinearLayout card=column();card.setPadding(12,14,12,14);GradientDrawable bg=new GradientDrawable();bg.setColor(Color.WHITE);bg.setCornerRadius(22);bg.setStroke(2,0xffdfd8c9);card.setBackground(bg);LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,0,0,16);list.addView(card,cp);
      TextView num=text("KITOB 0"+(i+1),12,gold);card.addView(num);TextView title=text(names[i],23,green);title.setTypeface(null,1);card.addView(title);card.addView(text(authors[i],14,0xff666666));int last=prefs.getInt("page"+i,0);card.addView(text(pages[i]+" sahifa   •   Davom etish: "+(last+1)+"-sahifa",14,0xff666666));button("O‘QISHNI BOSHLASH  ›",card,()->openBook(id));
    }
  }
  void openBook(int id){try{closePdf();book=id;File target=new File(getFilesDir(),"book"+id+".pdf");if(!target.exists()||target.length()==0){try(InputStream in=getAssets().open(files[id]);OutputStream out=new FileOutputStream(target)){byte[] buf=new byte[65536];int n;while((n=in.read(buf))!=-1)out.write(buf,0,n);}}descriptor=ParcelFileDescriptor.open(target,ParcelFileDescriptor.MODE_READ_ONLY);renderer=new PdfRenderer(descriptor);page=Math.max(0,Math.min(renderer.getPageCount()-1,prefs.getInt("page"+id,0)));zoom=1f;reader();}catch(Exception e){new AlertDialog.Builder(this).setMessage("Kitobni ochib bo‘lmadi: "+e.getMessage()).setPositiveButton("OK",null).show();}}
  void reader(){setup();header(names[book]);LinearLayout controls=new LinearLayout(this);controls.setGravity(Gravity.CENTER);root.addView(controls);button("‹ Kutubxona",controls,()->home());button("☆ Xatcho‘p",controls,()->{prefs.edit().putInt("mark"+book,page).apply();Toast.makeText(this,"Sahifa saqlandi",Toast.LENGTH_SHORT).show();});button("★ O‘tish",controls,()->goBookmark());
    ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));pageView=new ImageView(this);pageView.setAdjustViewBounds(true);pageView.setScaleType(ImageView.ScaleType.FIT_CENTER);scroll.addView(pageView,new ScrollView.LayoutParams(-1,-2));
    LinearLayout navigation=new LinearLayout(this);navigation.setGravity(Gravity.CENTER_VERTICAL);root.addView(navigation);button("◀",navigation,()->setPage(page-1));counter=text("",13,green);navigation.addView(counter,new LinearLayout.LayoutParams(0,-2,1));button("▶",navigation,()->setPage(page+1));
    seek=new SeekBar(this);seek.setMax(renderer.getPageCount()-1);root.addView(seek);seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean user){if(user)setPage(p);}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}});
    LinearLayout options=new LinearLayout(this);options.setGravity(Gravity.CENTER);root.addView(options);button("−",options,()->{zoom=Math.max(.7f,zoom-.2f);render();});button("+",options,()->{zoom=Math.min(2.5f,zoom+.2f);render();});button("Sahifaga o‘tish",options,()->jump());button("Tun / Kun",options,()->{dark=!dark;render();});render();
  }
  void jump(){EditText input=new EditText(this);input.setInputType(2);input.setHint("1–"+renderer.getPageCount());new AlertDialog.Builder(this).setTitle("Sahifa raqami").setView(input).setPositiveButton("O‘tish",(d,w)->{try{setPage(Integer.parseInt(input.getText().toString())-1);}catch(Exception ignored){}}).setNegativeButton("Bekor",null).show();}
  void goBookmark(){int mark=prefs.getInt("mark"+book,-1);if(mark<0)Toast.makeText(this,"Xatcho‘p hali yo‘q",Toast.LENGTH_SHORT).show();else setPage(mark);}
  void setPage(int p){if(renderer==null)return;page=Math.max(0,Math.min(renderer.getPageCount()-1,p));prefs.edit().putInt("page"+book,page).apply();render();}
  void render(){if(renderer==null||pageView==null)return;try(PdfRenderer.Page pdf=renderer.openPage(page)){int width=(int)(Math.min(1600,getResources().getDisplayMetrics().widthPixels*1.5f)*zoom);int height=(int)(width*(float)pdf.getHeight()/pdf.getWidth());Bitmap bitmap=Bitmap.createBitmap(Math.max(1,width),Math.max(1,height),Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(bitmap);canvas.drawColor(Color.WHITE);pdf.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);if(dark){Paint paint=new Paint();ColorMatrix matrix=new ColorMatrix(new float[]{-1,0,0,0,255,0,-1,0,0,255,0,0,-1,0,255,0,0,0,1,0});paint.setColorFilter(new android.graphics.ColorMatrixColorFilter(matrix));Bitmap inverted=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.ARGB_8888);new Canvas(inverted).drawBitmap(bitmap,0,0,paint);bitmap.recycle();bitmap=inverted;}pageView.setImageBitmap(bitmap);counter.setText((page+1)+" / "+renderer.getPageCount());seek.setProgress(page);}catch(Exception e){Toast.makeText(this,"Sahifa ochilmadi",Toast.LENGTH_SHORT).show();}}
  void closePdf(){if(renderer!=null){try{renderer.close();}catch(Exception ignored){}renderer=null;}if(descriptor!=null){try{descriptor.close();}catch(Exception ignored){}descriptor=null;}}
  @Override public void onBackPressed(){if(book>=0)home();else super.onBackPressed();}
  @Override protected void onDestroy(){closePdf();super.onDestroy();}
}
