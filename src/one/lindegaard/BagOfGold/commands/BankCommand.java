package one.lindegaard.BagOfGold.commands;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.util.Misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class BankCommand implements ICommand {

	private BagOfGold plugin;

	public BankCommand(BagOfGold plugin) {
		this.plugin = plugin;
	}

	// Admin command
	// /bag bank give <player> <amount> - to give the player an amount of bag of
	// gold.
	// Permission needed bagofgold.bank.give

	// /bag bank take <player> <amount> - to take an amount of money from the
	// player.
	// have in your hand.
	// Permission needed bagofgold.bank.take

	// /bag bank balance - to get your own bank balance
	// Permission needed bagofgold.bank.balance

	// /bag bank balance <player> - to get the players bank balance
	// Permission needed bagofgold.bank.balance.other

	@Override
	public String getName() {
		return "bank";
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " give <player>" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to give the player a " + plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
						+ " in his inventory.",

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " take <player>" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to take <amount> gold from the "
						+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
						+ " in the players inventory",

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " balance [optional playername]" + ChatColor.WHITE + " - to get your bankbalance of "
						+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim() };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.bank.description", "rewardname",
				plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim());
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		if (args.length == 1) {
			// /bag bank help
			// Show help
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
				return false;
		}

		if (args.length == 0
				|| (args.length >= 1 && (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")
						|| args[0].equalsIgnoreCase("bankbalance") || args[0].equalsIgnoreCase("bankbal")))) {
			// bag bank
			// bag bank balance
			// bag bank balance <player> to show the total amount of "bag of
			// gold" in the players bank account.

			if (sender.hasPermission("bagofgold.bank.balance") || sender.hasPermission("bagofgold.bank.*")) {
				OfflinePlayer offlinePlayer = null;
				boolean other = false;
				if (args.length <= 1) {
					if (!(sender instanceof Player)) {
						plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
								.getString("bagofgold.commands.base.noconsole", "command", "'bank balance'"));
						return true;
					} else
						offlinePlayer = (Player) sender;

				} else {
					if (sender.hasPermission("bagofgold.bank.balance.other")
							|| sender.hasPermission("bagofgold.bank.*")) {
						offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
						other = true;
					} else {
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
										"perm", "bagofgold.bank.balance.other", "command", "bank <playername>"));
						return true;
					}
				}

				double balance = plugin.getEconomyManager().bankBalance(offlinePlayer.getUniqueId().toString()).balance;

				if (other)
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString(
									"bagofgold.commands.money.bankbalance.other", "playername", offlinePlayer.getName(),
									"money", plugin.getEconomyManager().format(balance), "rewardname",
									ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
											+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()));
				else
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.money.bankbalance",
									"playername", "You", "money", plugin.getEconomyManager().format(balance),
									"rewardname",
									ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
											+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()));
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.bank.balance", "command", "bank"));
			}
			return true;

		} else if (args.length == 1 && Bukkit.getServer().getOfflinePlayer(args[0]) == null) {
			plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
					.getString("bagofgold.commands.base.unknown_playername", "playername", args[0]));
			return true;

		} else if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
			// /bag bank give <player> <amount>
			if (sender.hasPermission("bagofgold.bank.give") || sender.hasPermission("bagofgold.bank.*")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
					return true;
				}

				OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
				if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.playername-missing", "player", args[1]));
					return true;
				}

				if (args[2].matches("\\d+(\\.\\d+)?")) {
					double amount = Misc.round(Double.valueOf(args[2]));
					if (amount > plugin.getConfigManager().limitPerBag * 100) {
						amount = plugin.getConfigManager().limitPerBag * 100;
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
										"number", args[2], "maximum", amount));
					}
					plugin.getEconomyManager().bankDeposit(offlinePlayer.getUniqueId().toString(), amount);
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.bank.give", "command", "bank give"));
			}
			return true;
		} else if (args.length >= 2 && args[0].equalsIgnoreCase("take"))

		{
			// /bag bank take <player> <amount>
			if (sender.hasPermission("bagofgold.bank.take") || sender.hasPermission("bagofgold.bank.*")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
					return true;
				}
				OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
				if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
					return true;
				}
				if (args[2].matches("\\d+(\\.\\d+)?")) {
					double amount = Misc.round(Double.valueOf(args[2]));
					if (amount > plugin.getConfigManager().limitPerBag * 100) {
						amount = plugin.getConfigManager().limitPerBag * 100;
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
										"number", args[2], "maximum", amount));
					}
					plugin.getEconomyManager().bankWithdraw(offlinePlayer.getUniqueId().toString(), amount);
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.bank.take", "command", "bank take"));
			}
			return true;

		}

		return false;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 1) {
			items.add("give");
			items.add("take");
			items.add("balance");
		} else if (args.length == 2)
			for (Player player : Bukkit.getOnlinePlayers())
				items.add(player.getName());

		if (!args[args.length - 1].trim().isEmpty()) {
			String match = args[args.length - 1].trim().toLowerCase();

			items.removeIf(name -> !name.toLowerCase().startsWith(match));
		}
		return items;
	}
}
