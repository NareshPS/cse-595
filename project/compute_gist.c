/* Lear's GIST implementation, version 1.1, (c) INRIA 2009, Licence: GPL */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "gist.h"
#include "GistCalculator.h"

float* compute_gist(float *r, float *g, float *b, 
    int width, int height, int *desc_size) {
  int i = 0;
  int nblocks=4;
  int n_scale=3;
  int orientations_per_scale[50]={8,8,4};
 
  color_image_t *im=color_image_new(width,height);
  for (i = 0; i < width*height; ++i) {
    im->c1[i] = r[i];
    im->c2[i] = g[i];
    im->c3[i] = b[i];
  }
  
  float *desc = color_gist_scaletab(im, nblocks, n_scale,
      orientations_per_scale);

  *desc_size = 0;
  /* compute descriptor size */
  for (i = 0; i < n_scale; i++) {
    *desc_size+=nblocks*nblocks*orientations_per_scale[i];
  }

  *desc_size *= 3; /* color */

  // garbage collection
  color_image_delete(im);
  return desc; 
}

JNIEXPORT jfloatArray JNICALL Java_GistCalculator_calculateGist
  (JNIEnv *env, jobject obj, jfloatArray red, jfloatArray green,
  jfloatArray blue, jint width, jint height) {
  int i = 0;
  jfloat *red_vals = (jfloat*)(*env)->GetFloatArrayElements(env, red, 0);
  jfloat *green_vals = (jfloat*)(*env)->GetFloatArrayElements(env, green, 0);
  jfloat *blue_vals = (jfloat*)(*env)->GetFloatArrayElements(env, blue, 0);

  int desc_size = 0;
  float* gist = 
    compute_gist(red_vals, green_vals, blue_vals, width, height, &desc_size);
  jfloatArray ret = (*env)->NewFloatArray(env, desc_size);
  (*env)->SetFloatArrayRegion(env, ret, 0, desc_size, gist);
  free(gist);
  (*env)->ReleaseFloatArrayElements(env, red, red_vals, 0);
  (*env)->ReleaseFloatArrayElements(env, green, green_vals, 0);
  (*env)->ReleaseFloatArrayElements(env, blue, blue_vals, 0);
  return ret;
}
