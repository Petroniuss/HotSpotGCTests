include("plots.jl")

@df data plot(
  :when./1000, 
  :from .- :to, 
  xlabel="czas działania programu [s]", 
  ylabel="ilość odzyskanej pamięci [MB]", group=:gc
)
savefig("../reclaimed_over_time_22.png")

begin 
	sy1, sf1 = groupby(serial, :what)
	ty1, tf1 = groupby(theta, :what)
	@df theta plot(label="theta", ylabel="ilość odzyskanej pamięci [MB]", xaxis=nothing, legend=:topleft)
	@df ty1 boxplot!(:from.-:to, label="theta young")
	@df sy1 boxplot!(:from.-:to, label="serial young")
	@df tf1 boxplot!(:from.-:to, label="theta full")
	@df sf1 boxplot!(:from.-:to, label="serial full")
end
savefig("../reclaimed_box_22.png")

begin 
	sy, sf = groupby(serial, :what)
	ty, tf = groupby(theta, :what)
	@df theta plot(label="theta", ylabel="czas kolekcji [ms]", xaxis=nothing, legend=:topleft)
	@df ty boxplot!(:time, label="theta young")
	@df sy boxplot!(:time, label="serial young")
	@df tf boxplot!(:time, label="theta full")
	@df sf boxplot!(:time, label="serial full")
end
savefig("../collection_time.png")

begin
	plot(xlabel="ilość odzyskanej pamięci", ylabel="czas młodej kolekcji")
	@df ty scatter!(:from.-:to,:time, label="theta young")
	@df sy scatter!(:from.-:to,:time, label="serial young")
end
savefig("../young_collection_time_over_reclaimed.png")

begin
	plot(xlabel="czas działania programu [s]", ylabel="procentowy udział gc w czasie", legend=:bottomright)
	@df serial plot!(:when./1000, cumsum(:time)./:when, label="serial")
	@df theta plot!(:when./1000, cumsum(:time)./:when, label="theta")
end
savefig("../gc_time_over_time.png")