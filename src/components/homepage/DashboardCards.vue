<template>
  <v-row>
    <v-col v-for="card in cards" :key="card.title" cols="12" md="6" lg="3">
      <v-card>
        <v-card-title>{{ card.title }}</v-card-title>
        <v-card-text>
          <div class="text-h4 font-weight-bold">
            {{ card.value }}
          </div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
</template>

<script>
export default {
  props: {
    cards: {
      type: Array,
      required: true,
    },
  },
};
</script>
